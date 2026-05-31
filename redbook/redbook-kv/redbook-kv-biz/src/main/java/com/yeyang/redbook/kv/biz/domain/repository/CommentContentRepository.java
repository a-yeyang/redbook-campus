package com.yeyang.redbook.kv.biz.domain.repository;

import com.yeyang.redbook.kv.biz.domain.dataobject.CommentContentDO;
import com.yeyang.redbook.kv.biz.domain.dataobject.CommentContentPrimaryKey;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
public class CommentContentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CommentContentRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void saveAll(List<CommentContentDO> commentContentDOS) {
        jdbcTemplate.batchUpdate("""
                        INSERT INTO comment_content (note_id, `year_month`, content_id, content)
                        VALUES (?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE content = VALUES(content)
                        """,
                commentContentDOS,
                500,
                (ps, commentContentDO) -> {
                    CommentContentPrimaryKey primaryKey = commentContentDO.getPrimaryKey();
                    ps.setLong(1, primaryKey.getNoteId());
                    ps.setString(2, primaryKey.getYearMonth());
                    ps.setString(3, primaryKey.getContentId().toString());
                    ps.setString(4, commentContentDO.getContent());
                });
    }

    public List<CommentContentDO> findByPrimaryKeyNoteIdAndPrimaryKeyYearMonthInAndPrimaryKeyContentIdIn(
            Long noteId, List<String> yearMonths, List<UUID> contentIds
    ) {
        if (yearMonths.isEmpty() || contentIds.isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("noteId", noteId)
                .addValue("yearMonths", yearMonths)
                .addValue("contentIds", contentIds.stream().map(UUID::toString).toList());

        return namedParameterJdbcTemplate.query("""
                        SELECT note_id, `year_month`, content_id, content
                        FROM comment_content
                        WHERE note_id = :noteId
                          AND `year_month` IN (:yearMonths)
                          AND content_id IN (:contentIds)
                        """,
                parameters,
                (rs, rowNum) -> CommentContentDO.builder()
                        .primaryKey(CommentContentPrimaryKey.builder()
                                .noteId(rs.getLong("note_id"))
                                .yearMonth(rs.getString("year_month"))
                                .contentId(UUID.fromString(rs.getString("content_id")))
                                .build())
                        .content(rs.getString("content"))
                        .build());
    }

    public void deleteByPrimaryKeyNoteIdAndPrimaryKeyYearMonthAndPrimaryKeyContentId(
            Long noteId, String yearMonth, UUID contentId
    ) {
        jdbcTemplate.update("""
                        DELETE FROM comment_content
                        WHERE note_id = ?
                          AND `year_month` = ?
                          AND content_id = ?
                        """,
                noteId,
                yearMonth,
                contentId.toString());
    }
}
