package com.lest.modules.project.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 项目成员VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberVO {
    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 角色：admin / developer / observer
     */
    private String role;

    /**
     * 加入时间
     */
    private LocalDateTime joinedAt;
}
