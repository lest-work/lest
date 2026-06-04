package com.lest.common.plugin.manifest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.utils.StringUtils;
import com.lest.common.plugin.domain.PluginDescriptor;
import com.lest.common.plugin.extension.ExtensionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * manifest.json 解析器<br>
 * 负责解析插件根目录下的 manifest.json 文件，提取插件元信息和扩展声明。
 *
 * <p>manifest.json 格式：</p>
 * <pre>
 * {
 *   "pluginId": "lest-slack-notification",
 *   "name": "Slack 通知插件",
 *   "version": "1.0.0",
 *   "description": "任务变更自动通知到 Slack",
 *   "author": "team@example.com",
 *   "website": "https://example.com",
 *   "type": "backend",
 *   "order": 10,
 *   "config": {
 *     "slackWebhookUrl": ""
 *   },
 *   "extensions": [
 *     {
 *       "id": "slack-task-notify",
 *       "name": "Slack 任务通知",
 *       "point": "task.event",
 *       "implementation": "com.example.SlackTaskNotifier",
 *       "config": {
 *         "channels": ["#general"]
 *       }
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author yshan2028
 */
public class ManifestParser
{
    private static final Logger log = LoggerFactory.getLogger(ManifestParser.class);
    private static final String MANIFEST_FILE = "manifest.json";

    private final ObjectMapper objectMapper;

    public ManifestParser()
    {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 解析 classpath 资源中的 manifest.json
     */
    public Manifest parseFromClasspath(ClassLoader classLoader)
    {
        try (InputStream is = classLoader.getResourceAsStream(MANIFEST_FILE))
        {
            if (is == null)
            {
                log.warn("[ManifestParser] manifest.json not found in classpath");
                return null;
            }
            return parse(is);
        }
        catch (IOException e)
        {
            log.error("[ManifestParser] Failed to parse manifest.json from classpath: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析文件系统中的 manifest.json
     */
    public Manifest parseFromFile(Path pluginDir)
    {
        Path manifestPath = pluginDir.resolve(MANIFEST_FILE);
        if (!Files.exists(manifestPath))
        {
            log.warn("[ManifestParser] manifest.json not found at {}", manifestPath);
            return null;
        }
        try (InputStream is = Files.newInputStream(manifestPath))
        {
            return parse(is);
        }
        catch (IOException e)
        {
            log.error("[ManifestParser] Failed to parse manifest.json at {}: {}", manifestPath, e.getMessage());
            return null;
        }
    }

    /**
     * 解析 JAR 包中的 manifest.json
     */
    public Manifest parseFromJar(JarInputStream jarStream) throws IOException
    {
        JarEntry entry;
        while ((entry = jarStream.getNextJarEntry()) != null)
        {
            if (MANIFEST_FILE.equals(entry.getName()))
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = jarStream.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, len);
                }
                return parse(new ByteArrayInputStream(baos.toByteArray()));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Manifest parse(InputStream is) throws IOException
    {
        JsonNode root = objectMapper.readTree(is);
        Manifest manifest = new Manifest();

        // 基本信息
        manifest.setPluginId(getText(root, "pluginId"));
        manifest.setName(getText(root, "name"));
        manifest.setVersion(getText(root, "version"));
        manifest.setDescription(getText(root, "description"));
        manifest.setAuthor(getText(root, "author"));
        manifest.setWebsite(getText(root, "website"));
        manifest.setType(getText(root, "type"));
        manifest.setOrder(root.has("order") ? root.get("order").asInt() : 0);

        // config
        if (root.has("config") && root.get("config").isObject())
        {
            manifest.setConfig(objectMapper.convertValue(root.get("config"), Map.class));
        }

        // extensions
        if (root.has("extensions") && root.get("extensions").isArray())
        {
            List<ExtensionDescriptor> extensions = new ArrayList<>();
            for (JsonNode extNode : root.get("extensions"))
            {
                ExtensionDescriptor ext = new ExtensionDescriptor();
                ext.setId(getText(extNode, "id"));
                ext.setName(getText(extNode, "name"));
                ext.setPoint(getText(extNode, "point"));
                ext.setImplementation(getText(extNode, "implementation"));
                if (extNode.has("config") && extNode.get("config").isObject())
                {
                    ext.setConfig(objectMapper.convertValue(extNode.get("config"), Map.class));
                }
                extensions.add(ext);
            }
            manifest.setExtensions(extensions);
        }

        log.info("[ManifestParser] Parsed manifest: {} v{}", manifest.getPluginId(), manifest.getVersion());
        return manifest;
    }

    /**
     * 从 Manifest 构建 PluginDescriptor
     */
    public PluginDescriptor toDescriptor(Manifest manifest)
    {
        if (manifest == null) { return null; }
        PluginDescriptor descriptor = new PluginDescriptor();
        descriptor.setPluginId(manifest.getPluginId());
        descriptor.setName(manifest.getName());
        descriptor.setVersion(manifest.getVersion());
        descriptor.setDescription(manifest.getDescription());
        descriptor.setAuthor(manifest.getAuthor());
        descriptor.setWebsite(manifest.getWebsite());
        descriptor.setType(manifest.getType());
        descriptor.setOrder(manifest.getOrder());
        descriptor.setConfig(manifest.getConfig());
        return descriptor;
    }

    private String getText(JsonNode node, String field)
    {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    /**
     * manifest.json 结构
     */
    public static class Manifest
    {
        private String pluginId;
        private String name;
        private String version;
        private String description;
        private String author;
        private String website;
        private String type;
        private Integer order;
        private Map<String, Object> config;
        private List<ExtensionDescriptor> extensions;

        public String getPluginId() { return pluginId; }
        public void setPluginId(String pluginId) { this.pluginId = pluginId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getOrder() { return order; }
        public void setOrder(Integer order) { this.order = order; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public List<ExtensionDescriptor> getExtensions() { return extensions; }
        public void setExtensions(List<ExtensionDescriptor> extensions) { this.extensions = extensions; }
    }
}
