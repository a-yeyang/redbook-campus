package com.yeyang.redbook.comment.biz.consumer;

import cn.hutool.core.collection.CollUtil;
import com.yeyang.framework.common.util.JsonUtils;
import com.yeyang.redbook.comment.biz.constant.MQConstants;
import com.yeyang.redbook.comment.biz.domain.mapper.CommentLikeDOMapper;
import com.yeyang.redbook.comment.biz.enums.LikeUnlikeCommentTypeEnum;
import com.yeyang.redbook.comment.biz.model.dto.LikeUnlikeCommentMqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 评论点赞/取消点赞写库消费者。
 */
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "redbook_group_" + MQConstants.TOPIC_COMMENT_LIKE_OR_UNLIKE,
        topic = MQConstants.TOPIC_COMMENT_LIKE_OR_UNLIKE)
public class LikeUnlikeComment2DBConsumer implements RocketMQListener<String> {

    @Resource
    private CommentLikeDOMapper commentLikeDOMapper;

    @Override
    public void onMessage(String msgJson) {
        log.info("==> [comment like db] Consumer - Received message: {}", msgJson);
        try {
            LikeUnlikeCommentMqDTO dto = JsonUtils.parseObject(msgJson, LikeUnlikeCommentMqDTO.class);
            executeBatchSQL(List.of(dto));
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    private void executeBatchSQL(Collection<LikeUnlikeCommentMqDTO> values) {
        List<LikeUnlikeCommentMqDTO> likes = values.stream()
                .filter(op -> Objects.equals(op.getType(), LikeUnlikeCommentTypeEnum.LIKE.getCode()))
                .toList();

        List<LikeUnlikeCommentMqDTO> unlikes = values.stream()
                .filter(op -> Objects.equals(op.getType(), LikeUnlikeCommentTypeEnum.UNLIKE.getCode()))
                .toList();

        if (CollUtil.isNotEmpty(unlikes)) {
            commentLikeDOMapper.batchDelete(unlikes);
        }

        if (CollUtil.isNotEmpty(likes)) {
            commentLikeDOMapper.batchInsert(likes);
        }
    }
}
