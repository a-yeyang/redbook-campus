CREATE DATABASE IF NOT EXISTS redbook_app
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE redbook_app;

CREATE TABLE IF NOT EXISTS t_user (
  id BIGINT NOT NULL PRIMARY KEY,
  redbook_id VARCHAR(64) NOT NULL,
  `password` VARCHAR(255) NULL,
  nickname VARCHAR(128) NULL,
  avatar VARCHAR(512) NULL,
  birthday DATE NULL,
  background_img VARCHAR(512) NULL,
  email VARCHAR(128) NULL,
  sex TINYINT NULL,
  `status` TINYINT NOT NULL DEFAULT 0,
  introduction VARCHAR(512) NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted BIT NOT NULL DEFAULT b'0',
  UNIQUE KEY uk_user_redbook_id (redbook_id),
  UNIQUE KEY uk_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_role (
  id BIGINT NOT NULL PRIMARY KEY,
  role_name VARCHAR(64) NOT NULL,
  role_key VARCHAR(64) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 0,
  sort INT NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted BIT NOT NULL DEFAULT b'0',
  UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_user_role_rel (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted BIT NOT NULL DEFAULT b'0',
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_permission (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT NULL,
  `name` VARCHAR(64) NULL,
  `type` TINYINT NULL,
  menu_url VARCHAR(255) NULL,
  menu_icon VARCHAR(255) NULL,
  sort INT NOT NULL DEFAULT 0,
  permission_key VARCHAR(128) NULL,
  `status` TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted BIT NOT NULL DEFAULT b'0',
  KEY idx_permission_status (`status`, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_role_permission_rel (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted BIT NOT NULL DEFAULT b'0',
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_channel (
  id BIGINT NOT NULL PRIMARY KEY,
  `name` VARCHAR(64) NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted BIT NOT NULL DEFAULT b'0',
  UNIQUE KEY uk_channel_name (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_topic (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(64) NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted BIT NOT NULL DEFAULT b'0',
  UNIQUE KEY uk_topic_name (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_channel_topic_rel (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  channel_id BIGINT NOT NULL,
  topic_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_channel_topic (channel_id, topic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_note (
  id BIGINT NOT NULL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  is_content_empty BIT NOT NULL DEFAULT b'1',
  creator_id BIGINT NOT NULL,
  topic_id BIGINT NULL,
  topic_name VARCHAR(64) NULL,
  is_top BIT NOT NULL DEFAULT b'0',
  `type` TINYINT NOT NULL,
  img_uris TEXT NULL,
  video_uri VARCHAR(512) NULL,
  visible TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` TINYINT NOT NULL DEFAULT 1,
  content_uuid VARCHAR(64) NULL,
  channel_id BIGINT NOT NULL,
  topic_ids VARCHAR(512) NULL,
  KEY idx_note_creator (creator_id),
  KEY idx_note_channel (channel_id),
  KEY idx_note_status_visible (`status`, visible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_note_count (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  note_id BIGINT NOT NULL,
  like_total BIGINT NOT NULL DEFAULT 0,
  collect_total BIGINT NOT NULL DEFAULT 0,
  comment_total BIGINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_note_count_note_id (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_user_count (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  fans_total BIGINT NOT NULL DEFAULT 0,
  following_total BIGINT NOT NULL DEFAULT 0,
  note_total BIGINT NOT NULL DEFAULT 0,
  like_total BIGINT NOT NULL DEFAULT 0,
  collect_total BIGINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_user_count_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_note_like (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  note_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` TINYINT NOT NULL DEFAULT 1,
  UNIQUE KEY uk_note_like_user_note (user_id, note_id),
  KEY idx_note_like_note (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_note_collection (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  note_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` TINYINT NOT NULL DEFAULT 1,
  UNIQUE KEY uk_note_collection_user_note (user_id, note_id),
  KEY idx_note_collection_note (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_following (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  following_user_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_following_user_target (user_id, following_user_id),
  KEY idx_following_user_time (user_id, create_time),
  KEY idx_following_target (following_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_fans (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  fans_user_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_fans_user_target (user_id, fans_user_id),
  KEY idx_fans_user_time (user_id, create_time),
  KEY idx_fans_target (fans_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_comment (
  id BIGINT NOT NULL PRIMARY KEY,
  note_id BIGINT NULL,
  user_id BIGINT NULL,
  content_uuid VARCHAR(64) NULL,
  is_content_empty BIT NOT NULL DEFAULT b'1',
  image_url VARCHAR(512) NULL,
  `level` TINYINT NOT NULL DEFAULT 1,
  reply_total BIGINT NOT NULL DEFAULT 0,
  like_total BIGINT NOT NULL DEFAULT 0,
  parent_id BIGINT NOT NULL DEFAULT 0,
  reply_comment_id BIGINT NOT NULL DEFAULT 0,
  reply_user_id BIGINT NOT NULL DEFAULT 0,
  is_top BIT NOT NULL DEFAULT b'0',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  child_comment_total BIGINT NOT NULL DEFAULT 0,
  first_reply_comment_id BIGINT NOT NULL DEFAULT 0,
  heat DOUBLE NOT NULL DEFAULT 0,
  content VARCHAR(1024) NULL,
  avatar VARCHAR(512) NULL,
  nickname VARCHAR(128) NULL,
  mail VARCHAR(128) NULL,
  website VARCHAR(255) NULL,
  router_url VARCHAR(255) NULL,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  parent_comment_id BIGINT NULL,
  reason VARCHAR(255) NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  KEY idx_comment_note_level (note_id, `level`, heat),
  KEY idx_comment_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_comment_like (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  comment_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_comment_like_user_comment (user_id, comment_id),
  KEY idx_comment_like_comment (comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO t_role (id, role_name, role_key, `status`, sort, remark)
VALUES (1, 'common user', 'common_user', 0, 1, 'local default role');

INSERT IGNORE INTO t_channel (id, `name`)
VALUES
  (1, 'campus'),
  (2, 'study'),
  (3, 'life');
