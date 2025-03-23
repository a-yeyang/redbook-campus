package com.yeyang.redbook.note.biz.controller;

import com.yeyang.framework.biz.operationlog.aspect.ApiOperationLog;
import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.redbook.note.biz.model.vo.FindDiscoverNotePageListReqVO;
import com.yeyang.redbook.note.biz.model.vo.FindDiscoverNoteRspVO;
import com.yeyang.redbook.note.biz.service.DiscoverService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: coder
 * @date: 2025/2/4 13:22
 * @version: v1.0.0
 * @description: 发现页
 **/
@RestController
@RequestMapping("/discover")
@Slf4j
public class DiscoverController {

    @Resource
    private DiscoverService discoverService;

    @PostMapping(value = "/note/list")
    @ApiOperationLog(description = "发现页-查询笔记列表")
    public PageResponse<FindDiscoverNoteRspVO> findNoteList(@Validated @RequestBody FindDiscoverNotePageListReqVO findDiscoverNoteListReqVO) {
        return discoverService.findNoteList(findDiscoverNoteListReqVO);
    }

}
