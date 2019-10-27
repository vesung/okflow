package com.github.vesung.flow.controller;

import com.alibaba.fastjson.JSON;
import com.github.vesung.flow.FlowException;
import com.github.vesung.flow.FlowRole;
import com.github.vesung.flow.bean.FlowStepDto;
import com.github.vesung.flow.bean.Result;
import com.github.vesung.flow.persistence.dao.FlowDefMapper;
import com.github.vesung.flow.persistence.dao.FlowTypeDefMapper;
import com.github.vesung.flow.persistence.model.FlowDef;
import com.github.vesung.flow.persistence.model.FlowTypeDef;
import com.github.vesung.flow.service.FlowUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Resource
    private FlowTypeDefMapper flowTypeDefMapper;
    @Resource
    private FlowUserService userFindService;

    @ApiOperation("查询流程种类列表")
    @GetMapping(path = "/list/type")
    @ResponseBody
    public List<FlowTypeDef> listFlowType(){
         List<FlowTypeDef> types = flowTypeDefMapper.selectAll();
         return types;
    }

    @ApiOperation("查询流程步骤")
    @GetMapping(path = "/list/step/{type}")
    @ResponseBody
    public List<FlowDef> listStep(String type){
        return flowDefMapper.select(new FlowDef().setType(type));
    }

    @ApiOperation("更新流程定义")
    @PostMapping(path = "/update/flowtype")
    public void updateFlowType(@RequestBody FlowTypeDef flowTypeDef){
        FlowTypeDef def = flowTypeDefMapper.selectByPrimaryKey(flowTypeDef.getType());
        if(def == null){
            throw new FlowException("流程定义不存在");
        }

        flowTypeDefMapper.updateByPrimaryKey(def.setName(flowTypeDef.getName())
                .setName(flowTypeDef.getName())
                .setStatus("active")
                .setUpdate_date(new Date())
                .setUpdate_user(userFindService.currentUser().getAccount())
        );
    }

    @ApiOperation("查询角色列表")
    @GetMapping(path = "/list/role")
    @ResponseBody
    public List<FlowRole> listRole(){
        return userFindService.findRoles();
    }

    @ApiOperation("更新流程步骤")
    @PostMapping(path = "/update/step/{stepId}")
    @ResponseBody
    public Result<String> updateStep(@PathVariable Integer stepId,
                                     @RequestBody FlowStepDto dto){
        FlowDef step = this.assertNotNull(stepId);
        BeanUtils.copyProperties(dto, step);
        step.setUpdate_date(new Date()).setUpdate_user(userFindService.currentUser().getAccount());
        flowDefMapper.updateByPrimaryKey(step);

        return this.success();
    }

    @ApiOperation("查询下一步流程")
    @GetMapping(path = "/query/next/step/{stepId}")
    @ResponseBody
    public Map<String, String> queryNextStep(@PathVariable Integer stepId){
        FlowDef step = this.assertNotNull(stepId);
        return JSON.parseObject(step.getNext_step(), Map.class) ;
    }

    @ApiOperation("设置下一步流程")
    @PostMapping(path = "/update/next/step/{stepId}")
    @ResponseBody
    public Result<String> updateNextStep(@PathVariable Integer stepId,
                                         @RequestBody Map<String, String> nextStep){
        FlowDef step = this.assertNotNull(stepId);
        step.setNext_step(JSON.toJSONString(nextStep));
        step.setUpdate_date(new Date());
        step.setUpdate_user(userFindService.currentUser().getAccount());
        flowDefMapper.updateByPrimaryKey(step);
        return this.success();
    }


    private FlowDef assertNotNull(Integer stepId) {
        FlowDef step = flowDefMapper.selectByPrimaryKey(stepId);
        if(step == null){
            throw new FlowException("流程步骤不存在");
        }
        return step;
    }

//-----------------------------------------------------------------------------------------

    /**
     * 封装结果集
     * @param content
     * @return
     */
    protected <T> Result<T> success(T content, Class T) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMessage("成功");
        result.setData(content);
        return result;
    }

    protected Result<String> success(String msg){
        return this.success(msg, String.class);
    }

    protected Result<String> success(){
        return this.success("操作成功", String.class);
    }

}
