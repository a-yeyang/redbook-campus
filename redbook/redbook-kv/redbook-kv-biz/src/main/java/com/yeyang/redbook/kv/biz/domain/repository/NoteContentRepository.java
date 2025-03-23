package com.yeyang.redbook.kv.biz.domain.repository;

import com.yeyang.redbook.kv.biz.domain.dataobject.NoteContentDO;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

/**
 * @author: coder
 * @date: 2025/2/14 16:21
 * @version: v1.0.0
 * @description: TODO
 **/
public interface NoteContentRepository extends CassandraRepository<NoteContentDO, UUID> {

}
