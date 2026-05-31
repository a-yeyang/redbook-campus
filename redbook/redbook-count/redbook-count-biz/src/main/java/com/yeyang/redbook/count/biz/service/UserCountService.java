package com.yeyang.redbook.count.biz.service;

import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.count.dto.FindNoteCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindNoteCountByIdRspDTO;
import com.yeyang.redbook.count.dto.FindUserCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindUserCountByIdRspDTO;

/**
 * @author: yeyang
 * @date: 2025/4/7 15:41
 * @version: v1.0.0
 * @description: 用户计数业务
 **/
public interface UserCountService {

    Response<FindUserCountByIdRspDTO> findUserCountData(FindUserCountByIdReqDTO findUserCountByIdReqDTO);
}
