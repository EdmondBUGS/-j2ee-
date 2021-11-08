package com.xznu.edu.leave.action;

/**
 * 用户新增
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.xznu.edu.leave.model.Notice;
import com.xznu.edu.leave.model.Role;
import com.xznu.edu.leave.model.User;
import com.xznu.edu.leave.service.NoticeService;
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

@Controller("noticeAction")
@Scope("prototype")
public class NoticeAction extends ActionSupport implements ModelDriven<Notice> {
    @Autowired
    private NoticeService service;
    private Notice notice;
    private Map<String, Object> map = new HashMap();


    /**
     * list
     *
     * @return
     */
    public String list() throws IOException {
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null) {
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<Notice> pagers = service.getList(notice);
        Role role = user1.getRole();
        ActionContext.getContext().put("pagers", pagers);
        ActionContext.getContext().put("user", user1);
        ActionContext.getContext().put("role", role);
        return SUCCESS;
    }


    /**
     * 跳转add
     *
     * @return
     */
    public String add() {
        return SUCCESS;
    }

    /**
     * 查询修改
     *
     * @return
     */
    public String edit() {
        Notice bean = service.findById(notice.getId());
        ActionContext.getContext().put("bean", bean);
        return SUCCESS;
    }

    public String details() {
        Notice bean = service.findById(notice.getId());
        ActionContext.getContext().put("bean", bean);
        return SUCCESS;
    }

    /**
     * 更新
     *
     * @return
     */
    public void update() throws IOException {
        service.updates(notice);
        map.put("flag", true);
        map.put("url", "notice_list.do");
        JsonUtils.toJson(map);
    }

    /**
     * 保存
     *
     * @return
     */
    public void save() throws IOException {
        notice.setTime(new Date());
        service.save(notice);
        map.put("flag", true);
        map.put("url", "notice_list.do");
        JsonUtils.toJson(map);
    }

    public void delete() throws IOException {
        Notice bean = service.findById(notice.getId());
        bean.setIsDelete(1);
        service.update(bean);
        map.put("flag", true);
        map.put("url", "notice_list.do");
        JsonUtils.toJson(map);
    }

    @Override
    public Notice getModel() {
        if (notice == null) {
            notice = new Notice();
        }
        return notice;
    }
}
