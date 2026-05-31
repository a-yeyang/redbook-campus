package com.yeyang.redbook.note.biz.service;

import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.redbook.note.biz.model.vo.FindProfileNotePageListReqVO;
import com.yeyang.redbook.note.biz.model.vo.FindProfileNoteRspVO;

/**
 * @author: yeyang
 * @date: 2025/4/7 15:41
 * @version: v1.0.0
 * @description: 个人主页业务
 **/
public interface ProfileService {

    PageResponse<FindProfileNoteRspVO> findNoteList(FindProfileNotePageListReqVO findProfileNotePageListReqVO);
}
