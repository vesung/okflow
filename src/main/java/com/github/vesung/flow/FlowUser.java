package com.github.vesung.flow;

/**
 * 业务用户对象要实现本接口
 */
public interface FlowUser {

    /**
     * 用户id
     * @return
     */
    public String getAccount();

    /**
     * 用户所属机构
     * @return
     */
    String getDeptCode();

    /**
     * 用户角色
     * @return
     */
    String getRoleCode();

    public class block implements FlowUser {
        @Override
        public String getAccount() {
            return null;
        }

        @Override
        public String getDeptCode() {
            return null;
        }

        @Override
        public String getRoleCode() {
            return null;
        }
    }
}
