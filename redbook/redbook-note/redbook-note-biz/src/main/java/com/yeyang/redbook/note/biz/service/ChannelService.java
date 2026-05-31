package com.yeyang.redbook.note.biz.service;


import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.note.biz.model.vo.FindChannelRspVO;

import java.util.List;

/**
 * @author: yeyang
 * @date: 2025/4/7 15:41
 * @version: v1.0.0
 * @description: 频道业务
 **/
public interface ChannelService {

    /**
     * 查询所有频道
     * @return
     */
    Response<List<FindChannelRspVO>> findChannelList();
}
