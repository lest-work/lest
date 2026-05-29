package com.lest.modules.auth.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
