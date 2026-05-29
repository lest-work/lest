package com.lest.modules.file.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.file.entity.domain.FileRecord;
import com.lest.modules.file.entity.vo.FileRecordVO;
import com.lest.modules.file.service.FileRecordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileRecordService fileRecordService;

    @PostMapping("/upload")
    public Result<FileRecordVO> upload(@RequestParam("file") MultipartFile file) {
        FileRecordVO vo = fileRecordService.upload(file);
        return Result.ok(vo);
    }

    @PostMapping("/upload/base64")
    public Result<FileRecordVO> uploadBase64(
            @RequestParam("base64") String base64,
            @RequestParam(value = "fileName", required = false) String fileName) {
        FileRecordVO vo = fileRecordService.uploadBase64(base64, fileName);
        return Result.ok(vo);
    }

    @GetMapping("/list")
    public Result<PageResult<FileRecordVO>> list(FileRecord query) {
        return Result.ok(PageResult.of(fileRecordService.list(query)));
    }

    @GetMapping("/page")
    public Result<PageResult<FileRecordVO>> page(FileRecord query) {
        return Result.ok(PageResult.of(fileRecordService.page(query)));
    }

    @DeleteMapping("/remove/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        fileRecordService.remove(id);
        return Result.ok();
    }

    @DeleteMapping("/remove/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        fileRecordService.removeBatch(ids);
        return Result.ok();
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        try {
            FileRecordVO vo = fileRecordService.getById(id);
            if (vo == null) {
                response.setStatus(404);
                return;
            }

            response.setContentType("application/octet-stream");
            String fileName = URLEncoder.encode(vo.getName(), StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");

            try (InputStream is = fileRecordService.download(id);
                 OutputStream os = response.getOutputStream()) {
                is.transferTo(os);
            }
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    /**
     * 文件预览（浏览器内联打开，适用于图片/PDF/视频/音频）
     */
    @GetMapping("/preview/{id}")
    public void preview(@PathVariable Long id, HttpServletResponse response) {
        try {
            FileRecordVO vo = fileRecordService.getById(id);
            if (vo == null) {
                response.setStatus(404);
                return;
            }

            String contentType = vo.getContentType();
            if (contentType == null || contentType.equals("application/octet-stream")) {
                contentType = guessContentType(vo.getName());
            }
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline");

            try (InputStream is = fileRecordService.download(id);
                 OutputStream os = response.getOutputStream()) {
                is.transferTo(os);
            }
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    private String guessContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lower.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lower.endsWith(".webm")) {
            return "video/webm";
        } else if (lower.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (lower.endsWith(".wav")) {
            return "audio/wav";
        } else if (lower.endsWith(".ogg")) {
            return "audio/ogg";
        } else if (lower.endsWith(".doc") || lower.endsWith(".docx")) {
            return "application/msword";
        } else if (lower.endsWith(".xls") || lower.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else if (lower.endsWith(".ppt") || lower.endsWith(".pptx")) {
            return "application/vnd.ms-powerpoint";
        } else if (lower.endsWith(".txt")) {
            return "text/plain";
        }
        return "application/octet-stream";
    }
}
