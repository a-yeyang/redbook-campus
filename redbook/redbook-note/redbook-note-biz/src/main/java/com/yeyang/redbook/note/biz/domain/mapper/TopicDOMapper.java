package com.yeyang.redbook.note.biz.domain.mapper;

import com.yeyang.redbook.note.biz.domain.dataobject.TopicDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TopicDO record);

    int insertSelective(TopicDO record);

    int batchInsert(@Param("newTopics") List<TopicDO> newTopics);

    TopicDO selectByPrimaryKey(Long id);

    String selectNameByPrimaryKey(Long id);

    List<TopicDO> selectByLikeName(String keyword);

    List<TopicDO> selectByTopicIdIn(List<Long> topicIds);

    TopicDO selectByTopicName(String name);

    int updateByPrimaryKeySelective(TopicDO record);

    int updateByPrimaryKey(TopicDO record);

}