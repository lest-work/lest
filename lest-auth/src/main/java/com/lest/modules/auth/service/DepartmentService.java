package com.lest.modules.auth.service;

import com.lest.modules.auth.entity.dto.OrganizationDTO;
import com.lest.modules.auth.entity.vo.OrganizationVO;

import java.util.List;

/**
 * 部门服务接口（内部使用，与 OrganizationService 相同）
 * 为 lest-system 提供部门管理能力，通过 orgName -> deptName 的语义映射
 *
 * @author yshan2028
 */
public interface DepartmentService {

    OrganizationVO getById(Long id);

    List<OrganizationVO> listAll();

    List<OrganizationVO> getTree();

    Long create(OrganizationDTO dto);

    void update(OrganizationDTO dto);

    void delete(Long id);

    List<Long> getDeptIdsByRoleId(Long roleId);
}
