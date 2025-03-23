package com.yeyang.redbook.note.biz.consumer;

import com.yeyang.redbook.note.biz.constant.MQConstants;
import com.yeyang.redbook.note.biz.service.NoteService;
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
 * @description: 删除本地笔记缓存
 **/
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "redbook_group_" + MQConstants.TOPIC_DELETE_NOTE_LOCAL_CACHE, // Group
        topic = MQConstants.TOPIC_DELETE_NOTE_LOCAL_CACHE, // 消费的主题 Topic
        messageModel = MessageModel.BROADCASTING) // 广播模式
public class DeleteNoteLocalCacheConsumer implements RocketMQListener<String>  {

    @Resource
    private NoteService noteService;

    @Override
    public void onMessage(String body) {
        Long noteId = Long.valueOf(body);
        log.info("## 消费者消费成功, noteId: {}", noteId);

        noteService.deleteNoteLocalCache(noteId);
    }
}
