package com.lest.common.core.utils;

import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件工具类
 *
 * @author yshan2028
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * 获取文件扩展名（不含点）
     */
    public static String getExtension(String filename) {
        return FileUtil.extName(filename);
    }

    /**
     * 获取文件主名（不含扩展名）
     */
    public static String getNameWithoutExtension(String filename) {
        return FileUtil.mainName(filename);
    }

    /**
     * 判断文件是否存在
     */
    public static boolean exists(String path) {
        return FileUtil.exist(path);
    }

    /**
     * 判断是否为图片文件
     */
    public static boolean isImage(String filename) {
        String ext = getExtension(filename);
        if (ext == null) {
            return false;
        }
        return ext.equalsIgnoreCase("jpg")
                || ext.equalsIgnoreCase("jpeg")
                || ext.equalsIgnoreCase("png")
                || ext.equalsIgnoreCase("gif")
                || ext.equalsIgnoreCase("bmp")
                || ext.equalsIgnoreCase("webp")
                || ext.equalsIgnoreCase("svg");
    }

    /**
     * 判断是否为视频文件
     */
    public static boolean isVideo(String filename) {
        String ext = getExtension(filename);
        if (ext == null) {
            return false;
        }
        return ext.equalsIgnoreCase("mp4")
                || ext.equalsIgnoreCase("avi")
                || ext.equalsIgnoreCase("mov")
                || ext.equalsIgnoreCase("wmv")
                || ext.equalsIgnoreCase("webm");
    }

    /**
     * 判断是否为音频文件
     */
    public static boolean isAudio(String filename) {
        String ext = getExtension(filename);
        if (ext == null) {
            return false;
        }
        return ext.equalsIgnoreCase("mp3")
                || ext.equalsIgnoreCase("wav")
                || ext.equalsIgnoreCase("ogg")
                || ext.equalsIgnoreCase("aac")
                || ext.equalsIgnoreCase("flac");
    }

    /**
     * 判断是否为 PDF 文件
     */
    public static boolean isPdf(String filename) {
        String ext = getExtension(filename);
        return ext != null && ext.equalsIgnoreCase("pdf");
    }

    /**
     * 判断是否为 Office 文档（doc/docx/xls/xlsx/ppt/pptx）
     */
    public static boolean isOffice(String filename) {
        String ext = getExtension(filename);
        if (ext == null) {
            return false;
        }
        return ext.equalsIgnoreCase("doc")
                || ext.equalsIgnoreCase("docx")
                || ext.equalsIgnoreCase("xls")
                || ext.equalsIgnoreCase("xlsx")
                || ext.equalsIgnoreCase("ppt")
                || ext.equalsIgnoreCase("pptx");
    }

    /**
     * 判断是否为可预览文件（图片/视频/音频/PDF）
     */
    public static boolean isPreviewable(String filename) {
        return isImage(filename) || isVideo(filename) || isAudio(filename) || isPdf(filename);
    }

    /**
     * 格式文件大小为人类可读字符串
     */
    public static String formatFileSize(long size) {
        return FileUtil.readableFileSize(size);
    }

    /**
     * MultipartFile 转换为 byte[]
     */
    public static byte[] toBytes(MultipartFile file) throws IOException {
        return file.getBytes();
    }

    /**
     * MultipartFile 转换为 InputStream
     */
    public static InputStream toInputStream(MultipartFile file) throws IOException {
        return file.getInputStream();
    }

    /**
     * 构建下载文件名（处理中文编码）
     */
    public static String encodeDownloadFileName(String filename, String userAgent) {
        try {
            if (userAgent != null && userAgent.contains("MSIE")) {
                return URLEncoder.encode(filename, StandardCharsets.UTF_8.name())
                        .replace("+", " ");
            }
            return URLEncoder.encode(filename, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return filename;
        }
    }

    /**
     * 根据 Content-Type 判断是否为图片
     */
    public static boolean isImageByContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/");
    }

    /**
     * 根据 Content-Type 判断是否为视频
     */
    public static boolean isVideoByContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("video/");
    }

    /**
     * 根据 Content-Type 判断是否为音频
     */
    public static boolean isAudioByContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("audio/");
    }

    /**
     * 根据 Content-Type 判断是否为 PDF
     */
    public static boolean isPdfByContentType(String contentType) {
        return "application/pdf".equalsIgnoreCase(contentType);
    }

    /**
     * 获取文件 MIME 类型
     */
    public static String getContentType(String filename) {
        try {
            Path path = Path.of(filename);
            return Files.probeContentType(path);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
