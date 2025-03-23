package com.yeyang.redbook.note.biz.controller;

import com.yeyang.framework.biz.operationlog.aspect.ApiOperationLog;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.note.biz.model.vo.*;
import com.yeyang.redbook.note.biz.service.ChannelService;
import com.yeyang.redbook.note.biz.service.NoteService;
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
 * @description: 频道
 **/
@RestController
@RequestMapping("/channel")
@Slf4j
public class ChannelController {

    @Resource
    private ChannelService channelService;

    @PostMapping(value = "/list")
    @ApiOperationLog(description = "获取所有频道")
    public Response<List<FindChannelRspVO>> findChannelList() {
        return channelService.findChannelList();
    }

}
