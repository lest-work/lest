package com.lest.common.plugin.extension;

/**
 * 插件扩展点接口<br>
 * 所有 LEST 平台插件扩展点必须继承此接口。<br>
 * 扩展点定义了插件可以插入系统功能的具体位置和契约。
 *
 * <p>使用方式：</p>
 * <pre>
 * // 定义扩展点
 * public interface TaskActionExtensionPoint extends ExtensionPoint {
 *     void beforeTaskCreate(Task task);
 *     void afterTaskCreate(Task task);
 * }
 *
 * // 插件实现扩展点
 * public class MyTaskPlugin implements Plugin, TaskActionExtensionPoint {
 *     public ExtensionPointDescriptor getExtensionPointDescriptor() {
 *         return ExtensionPointDescriptor.builder()
 *             .name("my-task-action")
 *             .description("自定义任务操作扩展")
 *             .build();
 *     }
 *     public void beforeTaskCreate(Task task) { ... }
 *     public void afterTaskCreate(Task task) { ... }
 * }
 * </pre>
 *
 * @author yshan2028
 */
public interface ExtensionPoint
{
    /**
     * 返回扩展点描述信息
     */
    default ExtensionPointDescriptor getExtensionPointDescriptor()
    {
        return ExtensionPointDescriptor.builder()
            .name(getClass().getSimpleName())
            .description("Extension point: " + getClass().getName())
            .build();
    }
}
