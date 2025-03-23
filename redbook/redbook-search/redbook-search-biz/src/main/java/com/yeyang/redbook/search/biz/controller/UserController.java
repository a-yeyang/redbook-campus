package com.yeyang.redbook.search.biz.controller;

import com.yeyang.framework.biz.operationlog.aspect.ApiOperationLog;
import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.dto.RebuildUserDocumentReqDTO;
import com.yeyang.redbook.search.biz.model.vo.SearchUserReqVO;
import com.yeyang.redbook.search.biz.model.vo.SearchUserRspVO;
import com.yeyang.redbook.search.biz.service.UserService;
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
 * @description: 用户搜索
 **/
@RestController
@RequestMapping("/search")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/user")
    @ApiOperationLog(description = "搜索用户")
    public PageResponse<SearchUserRspVO> searchUser(@RequestBody @Validated SearchUserReqVO searchUserReqVO) {
        return userService.searchUser(searchUserReqVO);
    }

    // ===================================== 对其他服务提供的接口 =====================================
    @PostMapping("/user/document/rebuild")
    @ApiOperationLog(description = "用户文档重建")
    public Response<Long> rebuildDocument(@Validated @RequestBody RebuildUserDocumentReqDTO rebuildUserDocumentReqDTO) {
        return userService.rebuildDocument(rebuildUserDocumentReqDTO);
    }

}
