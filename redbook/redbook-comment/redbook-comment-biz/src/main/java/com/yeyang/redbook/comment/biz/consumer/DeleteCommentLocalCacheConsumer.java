package com.yeyang.redbook.comment.biz.consumer;

import com.yeyang.redbook.comment.biz.constant.MQConstants;
import com.yeyang.redbook.comment.biz.service.CommentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author: coder
 * @url: www.yeyang.com
 * @date: 2025/3/30 11:27
 * @description: 删除本地评论缓存
 **/
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "redbook_group_" + MQConstants.TOPIC_DELETE_COMMENT_LOCAL_CACHE, // Group
        topic = MQConstants.TOPIC_DELETE_COMMENT_LOCAL_CACHE, // 消费的主题 Topic
        messageModel = MessageModel.BROADCASTING) // 广播模式
public class DeleteCommentLocalCacheConsumer implements RocketMQListener<String>  {

    @Resource
    private CommentService commentService;

    @Override
    public void onMessage(String body) {
        Long commentId = Long.valueOf(body);
        log.info("## 消费者消费成功, commentId: {}", commentId);

        commentService.deleteCommentLocalCache(commentId);
    }
}
