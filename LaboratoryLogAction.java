package com.xznu.edu.leave.action;

/**
 * 实验室租用
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

@Controller("laboratorylogAction")
@Scope("prototype")
public class LaboratoryLogAction extends ActionSupport implements ModelDriven<LaboratoryLog> {
    @Autowired
    private LaboratoryLogService laboratoryLogService;
    private LaboratoryLog log;

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
        Role role = user1.getRole();
        if (role.getEnName().equals("admin") || role.getEnName().equals("js")) {
            Pager<LaboratoryLog> pagers = laboratoryLogService.getList(log);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            ActionContext.getContext().put("laboratoryLog", log);
        }
        return SUCCESS;
    }


    @Override
    public LaboratoryLog getModel() {
        if (log == null) {
            log = new LaboratoryLog();
        }
        return log;
    }

}
