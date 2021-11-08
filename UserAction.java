package com.xznu.edu.leave.action;

/**
 * 用户新增
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.xznu.edu.leave.model.Role;
import com.xznu.edu.leave.model.User;
import com.xznu.edu.leave.service.RoleService;
import com.xznu.edu.leave.service.UserService;
import com.xznu.edu.leave.utils.JsonUtils;
import com.xznu.edu.leave.utils.Pager;
import com.xznu.edu.leave.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller("userAction")
@Scope("prototype")
public class UserAction extends ActionSupport implements ModelDriven<User> {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    private User user;
    private Integer userId;
    private Map<String, Object> map = new HashMap();


    /**
     * list
     *
     * @return
     */
    public String list() throws IOException {
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null){
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<User> pagers = null;
        Role role = user1.getRole();
        if (role.getEnName().equals("admin")) {
            pagers = userService.getList(user);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            ActionContext.getContext().put("role", role);
            ActionContext.getContext().put("bean", user);
            return SUCCESS;
        } else if (role.getEnName().equals("xs") || role.getEnName().equals("js")) {
            pagers = userService.getList(user1);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("bean", user);
            return SUCCESS;
        }
        return null;
    }



    /**
     * 跳转add
     *
     * @return
     */
    public String add() {
        Pager<Role> pagers = roleService.pagers();
        ActionContext.getContext().put("pagers", pagers);
        return SUCCESS;
    }

    /**
     * 查询修改
     *
     * @return
     */
    public String edit() {
        User bean = userService.findById(userId);
        Pager<Role> pagers = roleService.pagers();
        ActionContext.getContext().put("bean", bean);
        ActionContext.getContext().put("pagers", pagers);
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
    public String update() throws IOException {
        if (user.getPass().equals("")){
            user.setPass(null);
        }
        userService.updates(user);
        map.put("flag", true);
        map.put("url", "user_list.do");
        JsonUtils.toJson(map);
        return SUCCESS;
    }

    /**
     * 保存
     *
     * @return
     */
    public void save() throws IOException {
        if (userService.getUser(user) != null){
            map.put("flag", false);
            map.put("url", "login_login.do");
            JsonUtils.toJson(map);
        } else {
            user.setTime(new Date());
            userService.save(user);
            map.put("flag", true);
            map.put("url", "login_login.do");
            JsonUtils.toJson(map);
        }
    }

    public void delete() throws IOException {
        User user1 = userService.findById(userId);
        user1.setIsDelete(1);
        userService.update(user1);
        map.put("flag", true);
        map.put("url", "user_list.do");
        JsonUtils.toJson(map);
    }

    @Override
    public User getModel() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
