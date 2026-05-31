package com.yeyang.redbook.count.biz.service.impl;

import com.yeyang.framework.common.response.Response;
import com.yeyang.redbook.count.biz.domain.dataobject.NoteCountDO;
import com.yeyang.redbook.count.biz.domain.dataobject.UserCountDO;
import com.yeyang.redbook.count.biz.domain.mapper.NoteCountDOMapper;
import com.yeyang.redbook.count.biz.domain.mapper.UserCountDOMapper;
import com.yeyang.redbook.count.biz.service.NoteCountService;
import com.yeyang.redbook.count.biz.service.UserCountService;
import com.yeyang.redbook.count.dto.FindNoteCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindNoteCountByIdRspDTO;
import com.yeyang.redbook.count.dto.FindUserCountByIdReqDTO;
import com.yeyang.redbook.count.dto.FindUserCountByIdRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author: yeyang
 * @date: 2025/4/7 15:41
 * @version: v1.0.0
 * @description: 用户计数业务
 **/
@Service
@Slf4j
public class UserCountServiceImpl implements UserCountService {

    @Resource
    private UserCountDOMapper userCountDOMapper;

    @Override
    public Response<FindUserCountByIdRspDTO> findUserCountData(FindUserCountByIdReqDTO findUserCountByIdReqDTO) {
        Long userId = findUserCountByIdReqDTO.getUserId();

        FindUserCountByIdRspDTO findUserCountByIdRspDTO = FindUserCountByIdRspDTO.builder()
                .userId(userId)
                .build();

        UserCountDO userCountDO = userCountDOMapper.selectByUserId(userId);

        if (Objects.nonNull(userCountDO)) {
            findUserCountByIdRspDTO.setCollectTotal(userCountDO.getCollectTotal());
            findUserCountByIdRspDTO.setFansTotal(userCountDO.getFansTotal());
            findUserCountByIdRspDTO.setNoteTotal(userCountDO.getNoteTotal());
            findUserCountByIdRspDTO.setFollowingTotal(userCountDO.getFollowingTotal());
            findUserCountByIdRspDTO.setLikeTotal(userCountDO.getLikeTotal());
        }

        return Response.success(findUserCountByIdRspDTO);
    }
}
