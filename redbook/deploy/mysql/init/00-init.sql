CREATE DATABASE IF NOT EXISTS redbook_kv
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS leaf
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE redbook_kv;

CREATE TABLE IF NOT EXISTS note_content (
    id CHAR(36) NOT NULL,
    content LONGTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS comment_content (
    note_id BIGINT NOT NULL,
    `year_month` VARCHAR(16) NOT NULL,
    content_id CHAR(36) NOT NULL,
    content LONGTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (note_id, `year_month`, content_id),
    KEY idx_comment_content_id (content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

USE leaf;

CREATE TABLE IF NOT EXISTS leaf_alloc (
    biz_tag VARCHAR(128) NOT NULL,
    max_id BIGINT NOT NULL DEFAULT 1,
    step INT NOT NULL,
    description VARCHAR(256) DEFAULT NULL,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (biz_tag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO leaf_alloc (biz_tag, max_id, step, description)
VALUES
    ('leaf-segment-redbook-id', 1, 1000, 'redbook id'),
    ('leaf-segment-user-id', 1, 1000, 'user id'),
    ('leaf-segment-comment-id', 1, 1000, 'comment id')
ON DUPLICATE KEY UPDATE
    step = VALUES(step),
    description = VALUES(description);
