package com.lest.system.api.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 系统部门实体（无 MyBatis 注解，供跨服务共享）
 *
 * @author yshan2028
 */
@Data
public class SysDept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 机构编码
     */
    private String orgCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 排序号（与 orderNum 同义，兼容不同命名风格）
     */
    private Integer sort;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态: 1=正常, 0=禁用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;

    /**
     * 子部门
     */
    private List<SysDept> children;
}
