package com.github.vesung.flow.service;

import com.github.vesung.flow.IFlowUser;

import java.util.List;

public interface IUserFindService {

    /**
     * 按机构，角色查找指定用户
     * @param deptCode
     * @param roleCode
     * @return
     */
    List<IFlowUser> findUsers(String deptCode, String roleCode);

    /**
     * 按账号查找指定用户
     * @param account
     * @return
     */
    IFlowUser findUserByAccount(String account);
}
