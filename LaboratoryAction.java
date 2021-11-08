package com.xznu.edu.leave.action;

/**
 * 用户新增
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.xznu.edu.leave.model.Laboratory;
import com.xznu.edu.leave.model.LaboratoryLog;
import com.xznu.edu.leave.model.Role;
import com.xznu.edu.leave.model.User;
import com.xznu.edu.leave.service.LaboratoryLogService;
import com.xznu.edu.leave.service.LaboratoryService;
import com.xznu.edu.leave.service.RoleService;
import com.xznu.edu.leave.service.UserService;
import com.xznu.edu.leave.utils.JsonUtils;
import com.xznu.edu.leave.utils.Pager;
import com.xznu.edu.leave.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller("laboratoryAction")
@Scope("prototype")
public class LaboratoryAction extends ActionSupport implements ModelDriven<Laboratory> {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private LaboratoryService laboratoryService;
    @Autowired
    private LaboratoryLogService laboratoryLogService;
    private User user;
    private Laboratory laboratory;
    private Integer laboratoryId;
    private Map<String, Object> map = new HashMap();


    /**
     * list
     *
     * @return
     */
    public String list(){
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null){
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<Laboratory> pagers = null;
        Role role = user1.getRole();
        if (role.getEnName().equals("admin") || role.getEnName().equals("js")) {
            pagers = laboratoryService.getList(laboratory);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            ActionContext.getContext().put("laboratory", laboratory);
        }
        return SUCCESS;
    }

    /**
     * list2
     *
     * @return
     */
    public String list2(){
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null){
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<Laboratory> pagers = null;
        Role role = user1.getRole();
        if (role.getEnName().equals("admin") || role.getEnName().equals("js")) {
            pagers = laboratoryService.getList(laboratory);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            ActionContext.getContext().put("laboratory", laboratory);
        }
        return SUCCESS;
    }

    /**
     * 预约
     *
     * @return
     */
    public void yy() throws IOException {
        laboratory.setIsYy(1);
        laboratory.setUser(UserUtils.getUser());
        LaboratoryLog laboratoryLog = new LaboratoryLog();
        laboratoryLog.setIsYy(1);
        laboratoryLog.setLaboratory(laboratory);
        laboratoryLog.setTime(new Date());
        laboratoryLog.setUser(UserUtils.getUser());
        laboratoryLogService.save(laboratoryLog);
        laboratory.setLaboratoryLogId(laboratoryLog.getId());
        laboratoryService.updates(laboratory);
        map.put("flag", true);
        map.put("url", "laboratory_list2.do");
        JsonUtils.toJson(map);
    }

    /**
     * 取消预约
     *
     * @return
     */
    public void qx() throws IOException {
        Laboratory laboratory1 = laboratoryService.findById(laboratory.getId());
        LaboratoryLog laboratoryLog = laboratoryLogService.findById(laboratory1.getLaboratoryLogId());
        if (laboratoryLog.getUser().getId() == UserUtils.getUser().getId()){
            laboratory1.setIsYy(0);
            laboratory1.setUser(null);
            laboratoryService.update(laboratory1);
            laboratoryLog.setIsYy(0);
            laboratoryLog.setEndTime(new Date());
            laboratoryLog.setQx(UserUtils.getUser());
            laboratoryLogService.updates(laboratoryLog);
            map.put("flag", true);
            map.put("url", "laboratory_list2.do");
            JsonUtils.toJson(map);
        } else {
            map.put("flag", false);
            map.put("url", "laboratory_list2.do");
            JsonUtils.toJson(map);
        }

    }

    /**
     * 跳转add
     *
     * @return
     */
    public String add(){
        User user1 = UserUtils.getUser();
        ActionContext.getContext().put("bean", user1);
        return SUCCESS;
    }

    /**
     * 查询修改
     *
     * @return
     */
    public String edit() {
        if (laboratoryId != null && !laboratoryId.equals("")){
            Laboratory bean = laboratoryService.findById(laboratoryId);
            ActionContext.getContext().put("bean", bean);
        }
        return SUCCESS;
    }


    /**
     * 审核
     *
     * @return
     */
    public void updateSh() throws IOException {
        user.setIsSh(1);
        userService.updates(user);
        map.put("flag", true);
        map.put("url", "user_list.do");
        JsonUtils.toJson(map);
    }

    /**
     * 更新
     *
     * @return
     */
    public void update() throws IOException {
        laboratoryService.updates(laboratory);
        map.put("flag", true);
        map.put("url", "laboratory_list.do");
        JsonUtils.toJson(map);
    }

    /**
     * 保存
     *
     * @return
     */
    public String save() throws IOException {
        laboratory.setTime(new Date());
        laboratoryService.save(laboratory);
        map.put("flag", true);
        map.put("url", "laboratory_list.do");
        JsonUtils.toJson(map);
        return SUCCESS;
    }

    public void delete() throws IOException {
        laboratory.setIsDelete(1);
        laboratoryService.updates(laboratory);
        map.put("flag", true);
        map.put("url", "laboratory_list.do");
        JsonUtils.toJson(map);
    }

    @Override
    public Laboratory getModel() {
        if (laboratory == null) {
            laboratory = new Laboratory();
        }
        return laboratory;
    }

    public Integer getLaboratoryId() {
        return laboratoryId;
    }

    public void setLaboratoryId(Integer laboratoryId) {
        this.laboratoryId = laboratoryId;
    }

}
