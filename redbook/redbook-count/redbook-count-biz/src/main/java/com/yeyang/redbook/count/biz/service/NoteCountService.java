package com.yeyang.redbook.count.biz.service;

import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.count.dto.FindNoteCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindNoteCountByIdRspDTO;

/**
 * @author: yeyang
 * @date: 2025/4/7 15:41
 * @version: v1.0.0
 * @description: 笔记计数业务
 **/
public interface NoteCountService {

    /**
     * 查询笔记计数数据
     * @param findNoteCountByIdReqDTO
     * @return
     */
    Response<FindNoteCountByIdRspDTO> findNoteCountData(FindNoteCountByIdReqDTO findNoteCountByIdReqDTO);
}
