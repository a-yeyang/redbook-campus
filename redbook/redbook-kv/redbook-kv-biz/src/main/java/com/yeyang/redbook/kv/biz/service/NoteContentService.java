package com.yeyang.redbook.kv.biz.service;


import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.kv.dto.req.AddNoteContentReqDTO;
import com.yeyang.redbook.kv.dto.req.DeleteNoteContentReqDTO;
import com.yeyang.redbook.kv.dto.req.FindNoteContentReqDTO;
import com.yeyang.redbook.kv.dto.rsp.FindNoteContentRspDTO;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: 笔记内容存储业务
 **/
public interface NoteContentService {

    /**
     * 添加笔记内容
     *
     * @param addNoteContentReqDTO
     * @return
     */
    Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO);

    /**
     * 查询笔记内容
     *
     * @param findNoteContentReqDTO
     * @return
     */
    Response<FindNoteContentRspDTO> findNoteContent(FindNoteContentReqDTO findNoteContentReqDTO);


    /**
     * 删除笔记内容
     *
     * @param deleteNoteContentReqDTO
     * @return
     */
    Response<?> deleteNoteContent(DeleteNoteContentReqDTO deleteNoteContentReqDTO);

}
