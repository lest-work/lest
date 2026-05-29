package com.lest.modules.auth.service;

import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.OrganizationDTO;
import com.lest.modules.auth.entity.vo.OrganizationVO;

import java.util.List;

/**
 * 机构服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface OrganizationService {

    PageResult<OrganizationVO> page(Integer page, Integer size, String orgName);
    List<OrganizationVO> getTree();
    OrganizationVO getById(Long id);
    List<OrganizationVO> listAll();
    Long create(OrganizationDTO dto);
    void update(OrganizationDTO dto);
    void delete(Long id);
}
