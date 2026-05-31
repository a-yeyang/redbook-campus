package com.yeyang.redbook.search.biz.service;

import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.dto.RebuildUserDocumentReqDTO;
import com.yeyang.redbook.search.biz.model.vo.SearchUserReqVO;
import com.yeyang.redbook.search.biz.model.vo.SearchUserRspVO;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: 用户搜索业务
 **/
public interface UserService {

    /**
     * 搜索用户
     * @param searchUserReqVO
     * @return
     */
    PageResponse<SearchUserRspVO> searchUser(SearchUserReqVO searchUserReqVO);

    /**
     * 重建用户文档
     * @param rebuildUserDocumentReqDTO
     * @return
     */
    Response<Long> rebuildDocument(RebuildUserDocumentReqDTO rebuildUserDocumentReqDTO);
}
