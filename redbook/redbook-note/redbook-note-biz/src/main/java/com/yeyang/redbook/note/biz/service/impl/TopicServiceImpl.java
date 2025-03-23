package com.yeyang.redbook.note.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.note.biz.domain.dataobject.ChannelDO;
import com.yeyang.redbook.note.biz.domain.dataobject.TopicDO;
import com.yeyang.redbook.note.biz.domain.mapper.ChannelDOMapper;
import com.yeyang.redbook.note.biz.domain.mapper.TopicDOMapper;
import com.yeyang.redbook.note.biz.model.vo.FindChannelRspVO;
import com.yeyang.redbook.note.biz.model.vo.FindTopicListReqVO;
import com.yeyang.redbook.note.biz.model.vo.FindTopicRspVO;
import com.yeyang.redbook.note.biz.service.ChannelService;
import com.yeyang.redbook.note.biz.service.TopicService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: 频道业务
 **/
@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    @Resource
    private TopicDOMapper topicDOMapper;

    @Override
    public Response<List<FindTopicRspVO>> findTopicList(FindTopicListReqVO findTopicListReqVO) {
        String keyword = findTopicListReqVO.getKeyword();

        List<TopicDO> topicDOS = topicDOMapper.selectByLikeName(keyword);

        List<FindTopicRspVO> findTopicRspVOS = null;
        if (CollUtil.isNotEmpty(topicDOS)) {
            findTopicRspVOS = topicDOS.stream()
                    .map(topicDO -> FindTopicRspVO.builder()
                            .id(topicDO.getId())
                            .name(topicDO.getName())
                            .build())
                    .toList();
        }

        return Response.success(findTopicRspVOS);
    }
}
