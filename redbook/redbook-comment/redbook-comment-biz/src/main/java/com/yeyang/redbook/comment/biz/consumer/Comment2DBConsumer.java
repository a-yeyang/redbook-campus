package com.yeyang.redbook.comment.biz.consumer;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.yeyang.framework.common.util.JsonUtils;
import com.yeyang.redbook.comment.biz.constant.MQConstants;
import com.yeyang.redbook.comment.biz.constant.RedisKeyConstants;
import com.yeyang.redbook.comment.biz.domain.dataobject.CommentDO;
import com.yeyang.redbook.comment.biz.domain.mapper.CommentDOMapper;
import com.yeyang.redbook.comment.biz.enums.CommentLevelEnum;
import com.yeyang.redbook.comment.biz.model.bo.CommentBO;
import com.yeyang.redbook.comment.biz.model.dto.CountPublishCommentMqDTO;
import com.yeyang.redbook.comment.biz.model.dto.PublishCommentMqDTO;
import com.yeyang.redbook.comment.biz.rpc.KeyValueRpcService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 评论写库消费者。
 */
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "redbook_group_" + MQConstants.TOPIC_PUBLISH_COMMENT,
        topic = MQConstants.TOPIC_PUBLISH_COMMENT)
public class Comment2DBConsumer implements RocketMQListener<String> {

    @Resource
    private CommentDOMapper commentDOMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private KeyValueRpcService keyValueRpcService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final RateLimiter rateLimiter = RateLimiter.create(1000);

    @Override
    public void onMessage(String msgJson) {
        log.info("==> Consumer - Received message: {}", msgJson);

        try {
            rateLimiter.acquire();
            PublishCommentMqDTO publishCommentMqDTO = JsonUtils.parseObject(msgJson, PublishCommentMqDTO.class);
            List<PublishCommentMqDTO> publishCommentMqDTOS = Lists.newArrayList(publishCommentMqDTO);

            List<Long> replyCommentIds = publishCommentMqDTOS.stream()
                    .filter(dto -> Objects.nonNull(dto.getReplyCommentId()))
                    .map(PublishCommentMqDTO::getReplyCommentId)
                    .toList();

            List<CommentDO> replyCommentDOS = null;
            if (CollUtil.isNotEmpty(replyCommentIds)) {
                replyCommentDOS = commentDOMapper.selectByCommentIds(replyCommentIds);
            }

            Map<Long, CommentDO> commentIdAndCommentDOMap = Maps.newHashMap();
            if (CollUtil.isNotEmpty(replyCommentDOS)) {
                commentIdAndCommentDOMap = replyCommentDOS.stream()
                        .collect(Collectors.toMap(CommentDO::getId, commentDO -> commentDO));
            }

            List<CommentBO> commentBOS = Lists.newArrayList();
            for (PublishCommentMqDTO dto : publishCommentMqDTOS) {
                String imageUrl = dto.getImageUrl();
                CommentBO commentBO = CommentBO.builder()
                        .id(dto.getCommentId())
                        .noteId(dto.getNoteId())
                        .userId(dto.getCreatorId())
                        .isContentEmpty(true)
                        .imageUrl(StringUtils.isBlank(imageUrl) ? "" : imageUrl)
                        .level(CommentLevelEnum.ONE.getCode())
                        .parentId(dto.getNoteId())
                        .createTime(dto.getCreateTime())
                        .updateTime(dto.getCreateTime())
                        .isTop(false)
                        .replyTotal(0L)
                        .likeTotal(0L)
                        .replyCommentId(0L)
                        .replyUserId(0L)
                        .build();

                String content = dto.getContent();
                if (StringUtils.isNotBlank(content)) {
                    commentBO.setContentUuid(UUID.randomUUID().toString());
                    commentBO.setIsContentEmpty(false);
                    commentBO.setContent(content);
                }

                Long replyCommentId = dto.getReplyCommentId();
                if (Objects.nonNull(replyCommentId)) {
                    CommentDO replyCommentDO = commentIdAndCommentDOMap.get(replyCommentId);

                    if (Objects.nonNull(replyCommentDO)) {
                        commentBO.setLevel(CommentLevelEnum.TWO.getCode());
                        commentBO.setReplyCommentId(replyCommentId);
                        commentBO.setParentId(replyCommentDO.getId());
                        if (Objects.equals(replyCommentDO.getLevel(), CommentLevelEnum.TWO.getCode())) {
                            commentBO.setParentId(replyCommentDO.getParentId());
                        }
                        commentBO.setReplyUserId(replyCommentDO.getUserId());
                    }
                }

                commentBOS.add(commentBO);
            }

            log.info("## cleaned CommentBOS: {}", JsonUtils.toJsonString(commentBOS));

            Integer insertedRows = transactionTemplate.execute(status -> {
                try {
                    int count = commentDOMapper.batchInsert(commentBOS);

                    List<CommentBO> commentContentNotEmptyBOS = commentBOS.stream()
                            .filter(commentBO -> Boolean.FALSE.equals(commentBO.getIsContentEmpty()))
                            .toList();
                    if (CollUtil.isNotEmpty(commentContentNotEmptyBOS)) {
                        keyValueRpcService.batchSaveCommentContent(commentContentNotEmptyBOS);
                    }

                    return count;
                } catch (Exception ex) {
                    status.setRollbackOnly();
                    log.error("", ex);
                    throw ex;
                }
            });

            if (Objects.nonNull(insertedRows) && insertedRows > 0) {
                syncOneLevelComment2RedisZSet(commentBOS);
                sendCountMessage(commentBOS);
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    private void sendCountMessage(List<CommentBO> commentBOS) {
        List<CountPublishCommentMqDTO> countPublishCommentMqDTOS = commentBOS.stream()
                .map(commentBO -> CountPublishCommentMqDTO.builder()
                        .noteId(commentBO.getNoteId())
                        .commentId(commentBO.getId())
                        .level(commentBO.getLevel())
                        .parentId(commentBO.getParentId())
                        .build())
                .toList();

        org.springframework.messaging.Message<String> message = MessageBuilder
                .withPayload(JsonUtils.toJsonString(countPublishCommentMqDTOS))
                .build();

        rocketMQTemplate.asyncSend(MQConstants.TOPIC_COUNT_NOTE_COMMENT, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> [count comment publish] MQ send success, SendResult: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> [count comment publish] MQ send failed", throwable);
            }
        });
    }

    private void syncOneLevelComment2RedisZSet(List<CommentBO> commentBOS) {
        Map<Long, List<CommentBO>> noteIdAndBOListMap = commentBOS.stream()
                .filter(commentBO -> Objects.equals(commentBO.getLevel(), CommentLevelEnum.ONE.getCode()))
                .collect(Collectors.groupingBy(CommentBO::getNoteId));

        noteIdAndBOListMap.forEach((noteId, commentBOList) -> {
            String key = RedisKeyConstants.buildCommentListKey(noteId);

            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/add_hot_comments.lua")));
            script.setResultType(Long.class);

            List<Object> args = Lists.newArrayList();
            commentBOList.forEach(commentBO -> {
                args.add(commentBO.getId());
                args.add(0);
            });

            redisTemplate.execute(script, Collections.singletonList(key), args.toArray());
        });
    }
}
