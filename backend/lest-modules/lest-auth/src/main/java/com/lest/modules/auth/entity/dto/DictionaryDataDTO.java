package com.lest.modules.auth.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryDataDTO {

    private Long id;

    private Long dictId;

    private String dictCode;

    private String dataKey;

    private String dataValue;

    private String label;

    private Integer sort;

    private Integer status;
}
