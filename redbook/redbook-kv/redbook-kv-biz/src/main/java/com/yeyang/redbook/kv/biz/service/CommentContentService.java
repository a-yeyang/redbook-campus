package com.yeyang.redbook.kv.biz.service;


import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.kv.dto.req.BatchAddCommentContentReqDTO;
import com.yeyang.redbook.kv.dto.req.BatchFindCommentContentReqDTO;
import com.yeyang.redbook.kv.dto.req.DeleteCommentContentReqDTO;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: 评论内容业务
 **/
public interface CommentContentService {


    /**
     * 批量添加评论内容
     * @param batchAddCommentContentReqDTO
     * @return
     */
    Response<?> batchAddCommentContent(BatchAddCommentContentReqDTO batchAddCommentContentReqDTO);

    /**
     * 批量查询评论内容
     * @param batchFindCommentContentReqDTO
     * @return
     */
    Response<?> batchFindCommentContent(BatchFindCommentContentReqDTO batchFindCommentContentReqDTO);

    /**
     * 删除评论内容
     * @param deleteCommentContentReqDTO
     * @return
     */
    Response<?> deleteCommentContent(DeleteCommentContentReqDTO deleteCommentContentReqDTO);

}
