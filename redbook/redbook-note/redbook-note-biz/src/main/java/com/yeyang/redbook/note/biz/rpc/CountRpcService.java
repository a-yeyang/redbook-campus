package com.yeyang.redbook.note.biz.rpc;

import cn.hutool.core.collection.CollUtil;
import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.count.api.CountFeignApi;
import com.yeyang.redbook.count.dto.FindNoteCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindNoteCountByIdRspDTO;
import com.yeyang.redbook.user.dto.req.FindUserByIdReqDTO;
import com.yeyang.redbook.user.dto.req.FindUsersByIdsReqDTO;
import com.yeyang.redbook.user.dto.resp.FindUserByIdRspDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author: yeyang
 * @date: 2025/4/13 23:29
 * @version: v1.0.0
 * @description: 计数服务
 **/
@Component
public class CountRpcService {

    @Resource
    private CountFeignApi countFeignApi;

    /**
     * 查询笔记计数信息
     * @param noteId
     * @return
     */
    public FindNoteCountByIdRspDTO findNoteCountById(Long noteId) {
        FindNoteCountByIdReqDTO findNoteCountByIdReqDTO = new FindNoteCountByIdReqDTO();
        findNoteCountByIdReqDTO.setNoteId(noteId);

        Response<FindNoteCountByIdRspDTO> response = countFeignApi.findNoteCount(findNoteCountByIdReqDTO);

        if (Objects.isNull(response) || !response.isSuccess()) {
            return null;
        }

        return response.getData();
    }

}
