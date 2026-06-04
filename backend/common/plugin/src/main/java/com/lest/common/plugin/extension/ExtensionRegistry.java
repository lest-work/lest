package com.lest.common.plugin.extension;

import com.lest.common.core.utils.StringUtils;
import com.lest.common.plugin.manifest.ManifestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展点注册表<br>
 * 统一管理所有插件声明的扩展点及其实现。
 * 供系统各模块在运行时查询和使用插件扩展。
 *
 * @author yshan2028
 */
@Component
public class ExtensionRegistry
{
    private static final Logger log = LoggerFactory.getLogger(ExtensionRegistry.class);

    /** 扩展点类型 -> 已注册的扩展实现列表 */
    private final Map<String, List<Extension>> registry = new ConcurrentHashMap<>();

    /** 扩展实例缓存 */
    private final Map<String, Extension> extensionInstances = new ConcurrentHashMap<>();

    /**
     * 注册一个扩展实现
     */
    public void register(Extension extension)
    {
        if (extension == null || extension.getDescriptor() == null) { return; }
        ExtensionDescriptor desc = extension.getDescriptor();
        String point = desc.getPoint();
        if (StringUtils.isEmpty(point)) { return; }

        registry.computeIfAbsent(point, k -> new ArrayList<>()).add(extension);
        extensionInstances.put(desc.getId(), extension);
        log.info("[ExtensionRegistry] Registered extension '{}' for point '{}'", desc.getId(), point);
    }

    /**
     * 注册来自 manifest 的扩展描述
     */
    public void registerFromManifest(ManifestParser.Manifest manifest)
    {
        if (manifest == null || manifest.getExtensions() == null) { return; }
        for (ExtensionDescriptor ext : manifest.getExtensions())
        {
            ext.setPluginId(manifest.getPluginId());
            SimpleExtension simple = new SimpleExtension(ext);
            register(simple);
        }
    }

    /**
     * 获取指定扩展点的所有扩展实现
     */
    public List<Extension> getExtensions(String point)
    {
        return registry.getOrDefault(point, Collections.emptyList());
    }

    /**
     * 获取指定扩展 ID 的扩展实例
     */
    public Extension getExtension(String id)
    {
        return extensionInstances.get(id);
    }

    /**
     * 获取所有已注册的扩展点类型
     */
    public Set<String> getAllPoints()
    {
        return new HashSet<>(registry.keySet());
    }

    /**
     * 获取指定插件的所有扩展
     */
    public List<Extension> getExtensionsByPlugin(String pluginId)
    {
        List<Extension> result = new ArrayList<>();
        for (Extension ext : extensionInstances.values())
        {
            if (pluginId.equals(ext.getDescriptor().getPluginId()))
            {
                result.add(ext);
            }
        }
        return result;
    }

    /**
     * 取消注册指定插件的所有扩展
     */
    public void unregisterByPlugin(String pluginId)
    {
        List<Extension> toRemove = getExtensionsByPlugin(pluginId);
        for (Extension ext : toRemove)
        {
            String point = ext.getDescriptor().getPoint();
            List<Extension> list = registry.get(point);
            if (list != null)
            {
                list.remove(ext);
            }
            extensionInstances.remove(ext.getDescriptor().getId());
        }
        log.info("[ExtensionRegistry] Unregistered {} extensions for plugin '{}'", toRemove.size(), pluginId);
    }

    /**
     * 基于 manifest 描述的轻量扩展实现
     */
    private static class SimpleExtension implements Extension
    {
        private final ExtensionDescriptor descriptor;

        SimpleExtension(ExtensionDescriptor descriptor) { this.descriptor = descriptor; }

        @Override
        public ExtensionDescriptor getDescriptor() { return descriptor; }
    }
}
