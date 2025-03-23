package com.yeyang.redbook.note.biz.service.impl;

import com.yeyang.framework.common.response.PageResponse;
import com.yeyang.redbook.note.biz.domain.dataobject.NoteDO;
import com.yeyang.redbook.note.biz.domain.mapper.NoteDOMapper;
import com.yeyang.redbook.note.biz.enums.NoteTypeEnum;
import com.yeyang.redbook.note.biz.model.vo.FindDiscoverNotePageListReqVO;
import com.yeyang.redbook.note.biz.model.vo.FindDiscoverNoteRspVO;
import com.yeyang.redbook.note.biz.rpc.UserRpcService;
import com.yeyang.redbook.note.biz.service.DiscoverService;
import com.yeyang.redbook.user.dto.resp.FindUserByIdRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: coder
 * @date: 2025/2/7 15:41
 * @version: v1.0.0
 * @description: 发现页业务
 **/
@Service
@Slf4j
public class DiscoverServiceImpl implements DiscoverService {

    @Resource
    private NoteDOMapper noteDOMapper;
    @Resource
    private UserRpcService userRpcService;


    @Override
    public PageResponse<FindDiscoverNoteRspVO> findNoteList(FindDiscoverNotePageListReqVO findDiscoverNoteListReqVO) {
        Long channelId = findDiscoverNoteListReqVO.getChannelId();
        Integer pageNo = findDiscoverNoteListReqVO.getPageNo();

        // 每页展示的数据量
        long pageSize = 10;

        // TODO: 为快速完成前后端联调，目前走数据库，后续需改成查询 Elasticserach

        int count = noteDOMapper.selectTotalCount(channelId);

        // 若评论总数为 0，则直接响应
        if (count == 0) {
            return PageResponse.success(null, pageNo, 0);
        }

        // 计算分页查询的偏移量 offset
        long offset = PageResponse.getOffset(pageNo, pageSize);
        long totalPage = PageResponse.getTotalPage(count, pageSize);

        // 若请求的页码大于总页数，直接响应
        if (pageNo > totalPage) {
            return PageResponse.success(null, pageNo, totalPage);
        }

        List<NoteDO> noteDOS = noteDOMapper.selectPageList(channelId, offset, pageSize);

        List<Long> creatorIds = noteDOS.stream().map(NoteDO::getCreatorId).toList();

        // RPC: 调用用户服务，批量获取用户信息（头像、昵称等）
        List<FindUserByIdRspDTO> findUserByIdRspDTOS = userRpcService.findByIds(creatorIds);
        Map<Long, FindUserByIdRspDTO> userIdAndDTOMap = findUserByIdRspDTOS.stream()
                .collect(Collectors.toMap(FindUserByIdRspDTO::getId, dto -> dto));


        // 分页返参
        List<FindDiscoverNoteRspVO> noteRspVOS = noteDOS.stream()
                .map(noteDO -> {
                    Integer type = noteDO.getType();
                    FindDiscoverNoteRspVO findDiscoverNoteRspVO = FindDiscoverNoteRspVO.builder()
                            .id(String.valueOf(noteDO.getId()))
                            .title(noteDO.getTitle())
                            .type(type)
                            .build();

                    NoteTypeEnum noteTypeEnum = NoteTypeEnum.valueOf(type);

                    switch (noteTypeEnum) {
                        case IMAGE_TEXT -> {
                            // 提取第一张图片作为封面图
                            String cover = Optional.ofNullable(noteDO.getImgUris())
                                    .map(uris -> StringUtils.split(uris, ",")[0])
                                    .orElse(null);
                            findDiscoverNoteRspVO.setCover(cover);
                        }
                        case VIDEO -> findDiscoverNoteRspVO.setVideoUri(noteDO.getVideoUri());
                    }

                    // 设置发布者信息
                    Long creatorId = noteDO.getCreatorId();
                    FindUserByIdRspDTO findUserByIdRspDTO = userIdAndDTOMap.get(creatorId);
                    if (Objects.nonNull(findUserByIdRspDTO)) {
                        findDiscoverNoteRspVO.setCreatorId(creatorId);
                        findDiscoverNoteRspVO.setNickname(findUserByIdRspDTO.getNickName());
                        findDiscoverNoteRspVO.setAvatar(findUserByIdRspDTO.getAvatar());
                    }

                    // TODO: 设置点赞数据

                    return findDiscoverNoteRspVO;
                })
                .toList();

        return PageResponse.success(noteRspVOS, pageNo, count, pageSize);
    }
}
