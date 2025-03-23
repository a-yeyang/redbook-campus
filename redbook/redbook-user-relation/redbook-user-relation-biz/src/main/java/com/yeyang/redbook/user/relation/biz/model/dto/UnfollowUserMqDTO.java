package com.yeyang.redbook.user.relation.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: coder
 * @date: 2025/2/7 15:17
 * @version: v1.0.0
 * @description: 取关用户
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnfollowUserMqDTO {

    private Long userId;

    private Long unfollowUserId;

    private LocalDateTime createTime;
}
