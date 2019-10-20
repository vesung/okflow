package com.github.vesung.flow.controller;

import com.github.vesung.flow.persistence.dao.FlowDefMapper;
import com.github.vesung.flow.persistence.model.FlowDef;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

/**
 * 流程配置管理Controller
 * @author wangjing.dc@qq.com
 * @since 2019/10/12
 */
@Controller
@RequestMapping("/okflow")
public class OkFlowController {

    @Resource
    private FlowDefMapper flowDefMapper;

    public void listFlowType(){
//        flowDefMapper.selectFlowType();
    }
}
