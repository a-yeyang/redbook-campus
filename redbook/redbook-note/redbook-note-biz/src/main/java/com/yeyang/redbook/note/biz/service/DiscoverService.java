package com.yeyang.redbook.note.biz.service;


import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.redbook.note.biz.model.vo.FindDiscoverNotePageListReqVO;
import com.yeyang.redbook.note.biz.model.vo.FindDiscoverNoteRspVO;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: 发现页业务
 **/
public interface DiscoverService {

    PageResponse<FindDiscoverNoteRspVO> findNoteList(FindDiscoverNotePageListReqVO findDiscoverNoteListReqVO);
}
