package com.lest.modules.system.entity.dto;

public record PostQueryDTO(
    Integer page,
    Integer size,
    String postCode,
    String postName,
    Integer status
) {}
