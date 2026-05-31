package com.yeyang.redbook.kv.biz;

import com.yeyang.framework.common.util.JsonUtils;
import com.yeyang.redbook.kv.biz.domain.dataobject.NoteContentDO;
import com.yeyang.redbook.kv.biz.domain.repository.NoteContentRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class JdbcNoteContentRepositoryTests {

    @Resource
    private NoteContentRepository noteContentRepository;

    @Test
    void testSaveAndDelete() {
        UUID id = UUID.randomUUID();
        NoteContentDO noteContent = NoteContentDO.builder()
                .id(id)
                .content("jdbc note content test")
                .build();

        noteContentRepository.save(noteContent);

        Optional<NoteContentDO> optional = noteContentRepository.findById(id);
        assertThat(optional).isPresent();
        assertThat(optional.get().getContent()).isEqualTo("jdbc note content test");
        optional.ifPresent(content -> log.info("query result: {}", JsonUtils.toJsonString(content)));

        noteContentRepository.deleteById(id);
        assertThat(noteContentRepository.findById(id)).isEmpty();
    }
}
