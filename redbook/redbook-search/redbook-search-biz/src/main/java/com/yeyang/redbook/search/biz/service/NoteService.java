package com.yeyang.redbook.search.biz.service;

import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.dto.RebuildNoteDocumentReqDTO;
import com.yeyang.redbook.search.biz.model.vo.SearchNoteReqVO;
import com.yeyang.redbook.search.biz.model.vo.SearchNoteRspVO;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: 笔记搜索业务
 **/
public interface NoteService {

    /**
     * 搜索笔记
     * @param searchNoteReqVO
     * @return
     */
    PageResponse<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO);

    /**
     * 重建笔记文档
     * @param rebuildNoteDocumentReqDTO
     * @return
     */
    Response<Long> rebuildDocument(RebuildNoteDocumentReqDTO rebuildNoteDocumentReqDTO);
}
