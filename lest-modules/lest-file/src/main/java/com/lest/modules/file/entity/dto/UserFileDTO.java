package com.lest.modules.file.entity.dto;

import lombok.Data;

@Data
public class UserFileDTO {
    private Long id;
    private Long userId;
    private String name;
    private Integer isDirectory;
    private Long parentId;
    private String path;
    private Long length;
    private String contentType;
}
