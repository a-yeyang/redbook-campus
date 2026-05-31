package com.yeyang.redbook.count.biz.controller;

import com.yeyang.framework.biz.operationlog.aspect.ApiOperationLog;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.count.biz.service.NoteCountService;
import com.yeyang.redbook.count.biz.service.UserCountService;
import com.yeyang.redbook.count.dto.FindNoteCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindNoteCountByIdRspDTO;
import com.yeyang.redbook.count.dto.FindUserCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindUserCountByIdRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author: yeyang
 * @date: 2025/4/4 13:22
 * @version: v1.0.0
 * @description: 用户维度计数
 **/
@RestController
@RequestMapping("/count")
@Slf4j
public class UserCountController {

    @Resource
    private UserCountService userCountService;

    @PostMapping(value = "/user/data")
    @ApiOperationLog(description = "获取用户计数数据")
    public Response<FindUserCountByIdRspDTO> findUserCountData(@Validated @RequestBody FindUserCountByIdReqDTO findUserCountByIdReqDTO) {
        return userCountService.findUserCountData(findUserCountByIdReqDTO);
    }

}
