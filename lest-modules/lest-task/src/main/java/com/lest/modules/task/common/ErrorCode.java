package com.lest.modules.task.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 * <p>
 * 错误码规则：
 * - 任务模块: 8000-8999
 * - 工时模块: 8100-8199
 * - 通用错误码: 9500-9999
 * </p>
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Getter
@AllArgsConstructor
public enum ErrorCode implements com.lest.common.core.ErrorCode {

    // ========== 任务模块 (8000-8999) ==========
    /** 任务不存在 */
    TASK_NOT_FOUND(8000, "任务不存在"),

    /** 任务已删除 */
    TASK_DELETED(8001, "任务已删除"),

    /** 任务已完成 */
    TASK_COMPLETED(8002, "任务已完成"),

    /** 任务存在依赖关系 */
    TASK_HAS_DEPENDENCIES(8003, "任务存在依赖关系"),

    /** 任务被其他任务阻塞 */
    TASK_BLOCKED_BY_OTHERS(8004, "任务被其他任务阻塞"),

    /** 父任务不存在 */
    TASK_PARENT_NOT_FOUND(8005, "父任务不存在"),

    /** 任务层级超限（最多3级） */
    TASK_DEPTH_EXCEEDED(8006, "任务层级超限（最多3级）"),

    /** 任务存在循环依赖 */
    TASK_CIRCULAR_DEPENDENCY(8007, "任务存在循环依赖"),

    /** 任务负责人不是项目成员 */
    TASK_ASSIGNEE_NOT_PROJECT_MEMBER(8008, "任务负责人不是项目成员"),

    /** 任务关联的提交不存在 */
    TASK_COMMIT_NOT_FOUND(8009, "任务关联的提交不存在"),

    /** 任务关联的MR不存在 */
    TASK_MR_NOT_FOUND(8010, "任务关联的MR不存在"),

    // ========== 工时模块 (8100-8199) ==========
    /** 工时记录不存在 */
    WORKLOG_NOT_FOUND(8100, "工时记录不存在"),

    /** 工时无效 */
    WORKLOG_HOURS_INVALID(8101, "工时无效"),

    // ========== 通用错误码 (9500-9999) ==========
    /** 标签不存在 */
    LABEL_NOT_FOUND(8200, "标签不存在"),

    /** 无效的认证令牌 */
    AUTH_TOKEN_INVALID(9500, "无效的认证令牌"),

    /** 参数校验失败 */
    VALIDATION_ERROR(9501, "参数校验失败"),

    /** 缺少必需参数 */
    PARAM_MISSING(9502, "缺少必需参数"),

    /** 参数类型错误 */
    PARAM_TYPE_ERROR(9503, "参数类型错误"),

    /** 无权限访问 */
    PERMISSION_DENIED(9504, "无权限访问"),

    /** 系统内部错误 */
    SYSTEM_ERROR(9999, "系统内部错误");

    /** 错误码 */
    private final int code;

    /** 错误消息 */
    private final String message;
}
