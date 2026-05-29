package com.lest.modules.system.service;

import com.lest.modules.system.entity.dto.UserDTO;
import com.lest.modules.system.entity.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author yshan2028
 */
public interface UserService {

    /**
     * 查询用户列表
     */
    List<UserVO> selectUserList(String username, String nickname, String phone, String email, Integer status, Long orgId);

    /**
     * 根据ID获取用户
     */
    UserVO getById(Long userId);

    /**
     * 新增用户
     */
    Long createUser(UserDTO dto);

    /**
     * 修改用户
     */
    void updateUser(UserDTO dto);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String password);

    /**
     * 修改用户状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 获取用户角色ID列表
     */
    String[] getUserRoleIds(Long userId);

    /**
     * 取消授权
     */
    void cancelAuthRole(Long userId, Long roleId);

    /**
     * 批量取消授权
     */
    void cancelAuthRoleAll(Long userId, Long[] roleIds);

    /**
     * 选择授权
     */
    void selectAuthRole(Long userId, Long[] roleIds);

    /**
     * 导出用户
     */
    void exportUser(List<UserVO> list);

    /**
     * 下载导入模板
     */
    void importTemplate();

    /**
     * 导入用户数据
     */
    String importUser(MultipartFile file, boolean updateSupport) throws Exception;
}
