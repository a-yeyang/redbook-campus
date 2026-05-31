package com.yeyang.redbook.note.biz.service;


import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.note.biz.model.vo.FindTopicListReqVO;
import com.yeyang.redbook.note.biz.model.vo.FindTopicRspVO;

import java.util.List;

/**
 * @author: yeyang
 * @date: 2025/4/7 15:41
 * @version: v1.0.0
 * @description: 话题业务
 **/
public interface TopicService {

    Response<List<FindTopicRspVO>> findTopicList(FindTopicListReqVO findTopicListReqVO);
}
