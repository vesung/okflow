package com.github.vesung.flow.service;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/11/11
 */
public interface FlowTimeHelper {

    /**
     * 计算当前步骤限时
     * @param step 步骤编码
     * @param buz_id 业务id
     * @param defaultTime
     * @return
     */
    Integer flowTime(String step, String buz_id, Integer defaultTime);
}
