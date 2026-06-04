package com.lest.common.plugin.extension;

/**
 * 预定义的 LEST 平台扩展点常量<br>
 * 定义了系统所有可扩展的扩展点标识符，插件通过 manifest.json 声明 point 字段来对接。
 *
 * @author yshan2028
 */
public final class ExtensionPoints
{
    private ExtensionPoints() {}

    // ===== Task 生命周期 =====
    /** 任务创建前 */
    public static final String TASK_BEFORE_CREATE = "task.before.create";
    /** 任务创建后 */
    public static final String TASK_AFTER_CREATE = "task.after.create";
    /** 任务状态变更前 */
    public static final String TASK_BEFORE_STATUS_CHANGE = "task.before.status.change";
    /** 任务状态变更后 */
    public static final String TASK_AFTER_STATUS_CHANGE = "task.after.status.change";
    /** 任务被删除前 */
    public static final String TASK_BEFORE_DELETE = "task.before.delete";
    /** 任务被删除后 */
    public static final String TASK_AFTER_DELETE = "task.after.delete";

    // ===== 事件通知 =====
    /** 任务事件通用扩展点 */
    public static final String TASK_EVENT = "task.event";
    /** 项目事件 */
    public static final String PROJECT_EVENT = "project.event";
    /** 迭代事件 */
    public static final String ITERATION_EVENT = "iteration.event";

    // ===== 自动化 =====
    /** 自动化规则执行 */
    public static final String AUTOMATION_RULE = "automation.rule";
    /** Webhook 触发 */
    public static final String WEBHOOK_TRIGGER = "webhook.trigger";

    // ===== UI 扩展 =====
    /** 任务详情页 Tab */
    public static final String TASK_DETAIL_TAB = "task.detail.tab";
    /** 项目侧边栏菜单 */
    public static final String PROJECT_SIDEBAR_MENU = "project.sidebar.menu";
}
