package com.yeyang.redbook.kv.biz.domain.repository;

import com.yeyang.redbook.kv.biz.domain.dataobject.NoteContentDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author: coder
 * @date: 2025/2/14 16:21
 * @version: v1.0.0
 * @description: TODO
 **/
@Repository
public class NoteContentRepository {

    private final JdbcTemplate jdbcTemplate;

    public NoteContentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(NoteContentDO noteContentDO) {
        jdbcTemplate.update("""
                        INSERT INTO note_content (id, content)
                        VALUES (?, ?)
                        ON DUPLICATE KEY UPDATE content = VALUES(content)
                        """,
                noteContentDO.getId().toString(),
                noteContentDO.getContent());
    }

    public Optional<NoteContentDO> findById(UUID id) {
        return jdbcTemplate.query("""
                        SELECT id, content
                        FROM note_content
                        WHERE id = ?
                        """,
                (rs, rowNum) -> NoteContentDO.builder()
                        .id(UUID.fromString(rs.getString("id")))
                        .content(rs.getString("content"))
                        .build(),
                id.toString())
                .stream()
                .findFirst();
    }

    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM note_content WHERE id = ?", id.toString());
    }
}
