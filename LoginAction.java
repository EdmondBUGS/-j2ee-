package com.xznu.edu.leave.action;
/**
 * 和登陆有关的都在这里
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.xznu.edu.leave.model.Role;
import com.xznu.edu.leave.model.User;
import com.xznu.edu.leave.service.RoleService;
import com.xznu.edu.leave.service.UserService;
import com.xznu.edu.leave.utils.JsonUtils;
import com.xznu.edu.leave.utils.UserUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller("loginAction")
@Scope("prototype")
public class LoginAction extends ActionSupport {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    private User user;
    private Map<String, Object> map = new HashMap();
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
 * 用户登陆
 * @return
 */
	public void index() throws IOException {
       User user1 = userService.getUser(user);
        if (user1 != null){
            if (user1.getIsSh() == 1){
                if (user1.getRole().getEnName().equals("admin")){
                    ActionContext.getContext().getSession().put("user", user1);
                }
                if (user1.getRole().getEnName().equals("js")){
                    ActionContext.getContext().getSession().put("user1", user1);
                }
                if (user1.getRole().getEnName().equals("xs")){
                    ActionContext.getContext().getSession().put("user2", user1);
                }
                map.put("flag", 1);
                map.put("url", "login_indexs.do");
                map.put("id", user1.getId());
                JsonUtils.toJson(map);
            } else {
                map.put("flag", 2);
                JsonUtils.toJson(map);
            }
        } else {
            map.put("flag", 3);
            JsonUtils.toJson(map);
        }
    }

    public String indexs() throws IOException {
        User u = UserUtils.getUser();
        if (u != null){
            ActionContext.getContext().put("user", u);
            String ss = u.getRole().getEnName();
            ActionContext.getContext().put("role", u.getRole().getEnName());
        }
        return SUCCESS;
    }
	//登陆页面
	public String login() {

        return SUCCESS;
	}

   //退出
	public String tuichu() {
		ActionContext ac = ActionContext.getContext();
		Map session = ac.getSession();
		session.remove("userName");
		session.remove("userId");
		return "login";
	}

}
