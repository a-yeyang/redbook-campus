package com.yeyang.redbook.count.biz.service.impl;

import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.count.biz.domain.dataobject.NoteCountDO;
import com.yeyang.redbook.count.biz.domain.mapper.NoteCountDOMapper;
import com.yeyang.redbook.count.biz.service.NoteCountService;
import com.yeyang.redbook.count.dto.FindNoteCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindNoteCountByIdRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author: yeyang
 * @date: 2025/4/7 15:41
 * @version: v1.0.0
 * @description: 笔记计数业务
 **/
@Service
@Slf4j
public class NoteCountServiceImpl implements NoteCountService {

    @Resource
    private NoteCountDOMapper noteCountDOMapper;

    /**
     * 查询笔记计数数据
     *
     * @param findNoteCountByIdReqDTO
     * @return
     */
    @Override
    public Response<FindNoteCountByIdRspDTO> findNoteCountData(FindNoteCountByIdReqDTO findNoteCountByIdReqDTO) {
        Long noteId = findNoteCountByIdReqDTO.getNoteId();

        // TODO: 后续需要添加缓存

        NoteCountDO noteCountDO = noteCountDOMapper.selectByNoteId(noteId);

        FindNoteCountByIdRspDTO findNoteCountByIdRspDTO = FindNoteCountByIdRspDTO.builder()
                .noteId(noteId)
                .collectTotal(0L)
                .commentTotal(0L)
                .likeTotal(0L)
                .build();

        if (Objects.nonNull(noteCountDO)) {
            findNoteCountByIdRspDTO.setCollectTotal(noteCountDO.getCollectTotal());
            findNoteCountByIdRspDTO.setCommentTotal(noteCountDO.getCommentTotal());
            findNoteCountByIdRspDTO.setLikeTotal(noteCountDO.getLikeTotal());
        }

        return Response.success(findNoteCountByIdRspDTO);
    }
}
