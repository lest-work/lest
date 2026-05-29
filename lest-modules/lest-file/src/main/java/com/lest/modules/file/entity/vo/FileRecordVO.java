package com.lest.modules.file.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRecordVO {
    private Long id;
    private String name;
    private String path;
    private Long length;
    private String contentType;
    private Long createUserId;
    private String createUsername;
    private String createNickname;
    @JsonProperty("createTime")
    private LocalDateTime createdAt;
    private String url;
    private String thumbnail;
    private String downloadUrl;
}
