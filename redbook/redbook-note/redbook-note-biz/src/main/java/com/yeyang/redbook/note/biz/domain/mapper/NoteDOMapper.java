package com.yeyang.redbook.note.biz.domain.mapper;

import com.yeyang.redbook.note.biz.domain.dataobject.NoteDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoteDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(NoteDO record);

    int insertSelective(NoteDO record);

    NoteDO selectByPrimaryKey(Long id);

    int selectCountByNoteId(Long noteId);

    /**
     * 查询笔记的发布者用户 ID
     * @param noteId
     * @return
     */
    Long selectCreatorIdByNoteId(Long noteId);

    int selectTotalCount(Long channelId);

    List<NoteDO> selectPageList(@Param("channelId") Long channelId,
                                @Param("offset") long offset,
                                @Param("pageSize") long pageSize);

    int updateByPrimaryKeySelective(NoteDO record);

    int updateByPrimaryKey(NoteDO record);

    int updateVisibleOnlyMe(NoteDO noteDO);

    int updateIsTop(NoteDO noteDO);

}