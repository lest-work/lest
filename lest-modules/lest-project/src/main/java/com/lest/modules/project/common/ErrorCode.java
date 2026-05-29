package com.lest.modules.project.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode implements com.lest.common.core.ErrorCode {
    // 项目模块错误码 (7000-7999)
    PROJECT_NOT_FOUND(7000, "项目不存在"),
    PROJECT_NAME_EXISTS(7001, "项目名称已存在"),
    PROJECT_HAS_ITERATIONS(7002, "项目存在关联的迭代，无法删除"),
    PROJECT_HAS_MEMBERS(7003, "项目存在关联的成员，无法删除"),
    PROJECT_ARCHIVED(7004, "项目已归档"),

    // 迭代模块错误码 (7100-7199)
    ITERATION_NOT_FOUND(7100, "迭代不存在"),
    ITERATION_DATE_CONFLICT(7101, "迭代时间与已有迭代冲突"),
    ITERATION_ALREADY_ACTIVE(7102, "迭代已处于进行中状态"),
    ITERATION_ALREADY_COMPLETED(7103, "迭代已处于已完成状态"),

    // 里程碑模块错误码 (7200-7299)
    MILESTONE_NOT_FOUND(7200, "里程碑不存在"),

    // 项目成员模块错误码 (7300-7399)
    PROJECT_MEMBER_NOT_FOUND(7300, "项目成员不存在"),
    PROJECT_MEMBER_EXISTS(7301, "项目成员已存在"),
    PROJECT_LAST_ADMIN(7302, "不能删除最后一个管理员"),
    PROJECT_LAST_ADMIN_ROLE(7303, "不能将最后一个管理员降级"),

    // 通用错误码
    SYSTEM_ERROR(9999, "系统内部错误");

    private final int code;
    private final String message;
}
