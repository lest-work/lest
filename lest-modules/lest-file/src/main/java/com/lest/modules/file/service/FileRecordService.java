package com.lest.modules.file.service;

import com.lest.modules.file.entity.domain.FileRecord;
import com.lest.modules.file.entity.vo.FileRecordVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileRecordService {
    FileRecordVO upload(MultipartFile file);
    FileRecordVO uploadBase64(String base64Data, String fileName);
    List<FileRecordVO> list(FileRecord query);
    List<FileRecordVO> page(FileRecord query);
    FileRecordVO getById(Long id);

    void remove(Long id);
    void removeBatch(List<Long> ids);
    InputStream download(Long id);
}
