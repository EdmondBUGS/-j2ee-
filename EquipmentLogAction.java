package com.xznu.edu.leave.action;

/**
 * 用户新增
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.xznu.edu.leave.model.*;
import com.xznu.edu.leave.service.EquipmentLogService;
import com.xznu.edu.leave.service.EquipmentService;
import com.xznu.edu.leave.service.LaboratoryService;
import com.xznu.edu.leave.service.RepairLogService;
import com.xznu.edu.leave.utils.JsonUtils;
import com.xznu.edu.leave.utils.Pager;
import com.xznu.edu.leave.utils.UUIDUtils;
import com.xznu.edu.leave.utils.UserUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("equipmentlogAction")
@Scope("prototype")
public class EquipmentLogAction extends ActionSupport implements ModelDriven<EquipmentLog> {
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private EquipmentLogService equipmentLogService;
    private EquipmentLog equipmentLog;
    private Map<String, Object> map = new HashMap();

    /**
     * list
     *
     * @return
     */
    public String list() {
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null) {
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Role role = user1.getRole();
        if (role.getEnName().equals("admin")) {
            Pager<EquipmentLog> pagers = equipmentLogService.getList(equipmentLog);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            ActionContext.getContext().put("equipmentLog", equipmentLog);
        }
        return SUCCESS;
    }

    public void export() throws IOException {
        List<EquipmentLog> list = equipmentLogService.getList(equipmentLog).getDatas();
        // 生成Workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        // 追加Sheet
        Sheet sheet = wb.createSheet("Sheet");
        // 总列数
        Integer CountColumnNum = 6;
        Cell[] firstCell = new Cell[CountColumnNum];
        String[] firstCellNames = new String[CountColumnNum];
        firstCellNames[0] = "设备型号";
        firstCellNames[1] = "借用人";
        firstCellNames[2] = "借用时间";
        firstCellNames[3] = "归还人";
        firstCellNames[4] = "归还时间";
        // 插入行
        Row firstRow = sheet.createRow(0);
        for (int j = 0; j < CountColumnNum; j++) {
            firstCell[j] = firstRow.createCell(j);
            firstCell[j].setCellValue(new HSSFRichTextString(firstCellNames[j]));
        }
        BigDecimal bd;
        for (int i = 0; i < list.size(); i++) {
            // 创建一行
            Row row = sheet.createRow(i + 1);
            Cell id = row.createCell(0);
            id.setCellValue(list.get(i).getEquipment().getXh().toString());
            Cell zzs = row.createCell(1);
            zzs.setCellValue(list.get(i).getUser().getRealName().toString());
            Cell xh = row.createCell(2);
            xh.setCellValue(list.get(i).getTime().toString());
            Cell gh = row.createCell(3);
            gh.setCellValue(list.get(i).getQx().getRealName().toString());
            Cell t = row.createCell(4);
            t.setCellValue(list.get(i).getEndTime().toString());
        }
        // 创建文件输出流，准备输出电子表格
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/vnd.ms-excel");//response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=export.xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.flush();
        out.close();
    }


    @Override
    public EquipmentLog getModel() {
        if (equipmentLog == null) {
            equipmentLog = new EquipmentLog();
        }
        return equipmentLog;
    }
}
