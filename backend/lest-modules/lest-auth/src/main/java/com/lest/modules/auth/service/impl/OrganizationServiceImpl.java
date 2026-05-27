package com.lest.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lest.common.base.Assert;
import com.lest.modules.auth.common.ErrorCode;
import com.lest.modules.auth.entity.domain.SysOrganization;
import com.lest.modules.auth.entity.domain.SysUser;
import com.lest.modules.auth.entity.dto.OrganizationDTO;
import com.lest.modules.auth.entity.vo.OrganizationVO;
import com.lest.modules.auth.mapper.SysOrganizationMapper;
import com.lest.modules.auth.mapper.SysUserMapper;
import com.lest.modules.auth.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机构服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private static final Long ROOT_ORG_ID = 0L;

    private final SysOrganizationMapper organizationMapper;
    private final SysUserMapper userMapper;

    @Override
    public List<OrganizationVO> getTree() {
        LambdaQueryWrapper<SysOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysOrganization::getSort).orderByDesc(SysOrganization::getCreatedAt);
        List<SysOrganization> allOrgs = organizationMapper.selectList(wrapper);
        return buildTree(allOrgs, ROOT_ORG_ID);
    }

    @Override
    public OrganizationVO getById(Long id) {
        SysOrganization org = organizationMapper.selectById(id);
        Assert.notNull(org, ErrorCode.ORG_NOT_FOUND);
        return convertToVO(org);
    }

    @Override
    public List<OrganizationVO> listAll() {
        LambdaQueryWrapper<SysOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysOrganization::getSort).orderByDesc(SysOrganization::getCreatedAt);
        return organizationMapper.selectList(wrapper).stream()
                .map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public Long create(OrganizationDTO dto) {
        Assert.isNull(organizationMapper.selectOne(
                new LambdaQueryWrapper<SysOrganization>()
                        .eq(SysOrganization::getOrgCode, dto.getOrgCode())
                        .eq(SysOrganization::getDeleted, 0)),
                ErrorCode.ORG_CODE_EXISTS);

        SysOrganization org = new SysOrganization();
        org.setParentId(dto.getParentId() != null ? dto.getParentId() : ROOT_ORG_ID);
        org.setOrgName(dto.getOrgName());
        org.setOrgCode(dto.getOrgCode());
        org.setSort(dto.getSort() != null ? dto.getSort() : 0);
        org.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        organizationMapper.insert(org);

        log.info("创建机构成功: orgId={}, orgName={}", org.getId(), dto.getOrgName());
        return org.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrganizationDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.ORG_NOT_FOUND);
        SysOrganization org = organizationMapper.selectById(dto.getId());
        Assert.notNull(org, ErrorCode.ORG_NOT_FOUND);

        if (dto.getOrgCode() != null && !dto.getOrgCode().equals(org.getOrgCode())) {
            Assert.isNull(organizationMapper.selectOne(
                    new LambdaQueryWrapper<SysOrganization>()
                            .eq(SysOrganization::getOrgCode, dto.getOrgCode())
                            .eq(SysOrganization::getDeleted, 0)
                            .ne(SysOrganization::getId, dto.getId())),
                    ErrorCode.ORG_CODE_EXISTS);
        }

        SysOrganization updateOrg = new SysOrganization();
        updateOrg.setId(dto.getId());
        if (dto.getParentId() != null) updateOrg.setParentId(dto.getParentId());
        if (dto.getOrgName() != null) updateOrg.setOrgName(dto.getOrgName());
        if (dto.getOrgCode() != null) updateOrg.setOrgCode(dto.getOrgCode());
        if (dto.getSort() != null) updateOrg.setSort(dto.getSort());
        if (dto.getStatus() != null) updateOrg.setStatus(dto.getStatus());

        organizationMapper.updateById(updateOrg);

        log.info("更新机构成功: orgId={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysOrganization org = organizationMapper.selectById(id);
        Assert.notNull(org, ErrorCode.ORG_NOT_FOUND);

        List<SysUser> users = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getOrgId, id)
                        .eq(SysUser::getDeleted, 0));
        Assert.isTrue(users.isEmpty(), ErrorCode.ORG_HAS_USERS);

        List<SysOrganization> children = organizationMapper.selectList(
                new LambdaQueryWrapper<SysOrganization>().eq(SysOrganization::getParentId, id));
        Assert.isTrue(children.isEmpty(), "机构下存在子机构，无法删除");

        organizationMapper.deleteById(id);

        log.info("删除机构成功: orgId={}", id);
    }

    /** 构建机构树 */
    private List<OrganizationVO> buildTree(List<SysOrganization> allOrgs, Long parentId) {
        return allOrgs.stream()
                .filter(org -> org.getParentId().equals(parentId))
                .sorted(Comparator.comparing(SysOrganization::getSort))
                .map(org -> {
                    OrganizationVO vo = convertToVO(org);
                    vo.setChildren(buildTree(allOrgs, org.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /** 实体转VO */
    private OrganizationVO convertToVO(SysOrganization org) {
        return OrganizationVO.builder()
                .id(org.getId())
                .parentId(org.getParentId())
                .orgName(org.getOrgName())
                .orgCode(org.getOrgCode())
                .sort(org.getSort())
                .status(org.getStatus())
                .createdAt(org.getCreatedAt())
                .updatedAt(org.getUpdatedAt())
                .build();
    }
}
