package com.yeyang.redbook.search.biz.controller;

import com.yeyang.framework.biz.operationlog.aspect.ApiOperationLog;
import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.dto.RebuildNoteDocumentReqDTO;
import com.yeyang.redbook.search.biz.model.vo.SearchNoteReqVO;
import com.yeyang.redbook.search.biz.model.vo.SearchNoteRspVO;
import com.yeyang.redbook.search.biz.service.NoteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author: coder
 * @date: 2025/2/4 13:22
 * @version: v1.0.0
 * @description: 笔记搜索
 **/
@RestController
@RequestMapping("/search")
@Slf4j
public class NoteController {

    @Resource
    private NoteService noteService;

    @PostMapping("/note")
    @ApiOperationLog(description = "搜索笔记")
    public PageResponse<SearchNoteRspVO> searchNote(@RequestBody @Validated SearchNoteReqVO searchNoteReqVO) {
        return noteService.searchNote(searchNoteReqVO);
    }

    // ===================================== 对其他服务提供的接口 =====================================
    @PostMapping("/note/document/rebuild")
    @ApiOperationLog(description = "用户文档重建")
    public Response<Long> rebuildDocument(@Validated @RequestBody RebuildNoteDocumentReqDTO rebuildNoteDocumentReqDTO) {
        return noteService.rebuildDocument(rebuildNoteDocumentReqDTO);
    }

}
