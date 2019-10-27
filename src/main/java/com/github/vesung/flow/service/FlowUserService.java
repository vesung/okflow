package com.github.vesung.flow.service;

import com.github.vesung.flow.FlowRole;
import com.github.vesung.flow.FlowUser;

import java.util.List;

public interface FlowUserService {

    /**
     * 按机构，角色查找指定用户
     * @param deptCode
     * @param roleCode
     * @return
     */
    List<FlowUser> findUsers(String deptCode, String roleCode);

    /**
     * 按账号查找指定用户
     * @param account
     * @return
     */
    FlowUser findUserByAccount(String account);

    /**
     * 查找当前登录用户
     * @return
     */
    FlowUser currentUser();

    /**
     * 查找角色列表
     * @return
     */
    List<FlowRole> findRoles();
}
