package com.lest.common.plugin.extension;

/**
 * 插件扩展接口<br>
 * 插件通过实现此接口并配合 manifest.json 声明的 extensions 字段来声明扩展点实现。
 *
 * @author yshan2028
 */
public interface Extension
{
    /**
     * 返回扩展描述
     */
    ExtensionDescriptor getDescriptor();

    /**
     * 扩展激活回调
     */
    default void onActive()
    {
    }

    /**
     * 扩展停用回调
     */
    default void onInactive()
    {
    }
}
