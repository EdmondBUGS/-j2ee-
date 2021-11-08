package com.xznu.edu.leave.action;

/**
 * 用户新增
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.xznu.edu.leave.model.*;
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

@Controller("repairAction")
@Scope("prototype")
public class RepairLogAction extends ActionSupport implements ModelDriven<RepairLog> {
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private RepairLogService repairLogService;
    @Autowired
    private LaboratoryService laboratoryService;
    private RepairLog repairLog;
    private Map<String, Object> map = new HashMap();
    private File file;
    private String fileFileName;
    private String fileContentType;
    private String downloadFileName;
    private Integer equipmentId;


    /**
     * 保修lis
     *
     * @return
     */
    public String repairlist() {
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null) {
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<RepairLog> pagers = null;
        Role role = user1.getRole();
        if (role.getEnName().equals("admin")) {
            ActionContext.getContext().put("user", user1);
            Pager<Equipment> pagers3 = equipmentService.getList(null);
            ActionContext.getContext().put("pagers3", pagers3);
        }
        return SUCCESS;
    }

    /**
     * 保修日志
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
            ActionContext.getContext().put("user", user1);
            Pager<RepairLog> pagers = repairLogService.getList(null);
            ActionContext.getContext().put("pagers", pagers);
        }
        return SUCCESS;
    }

    //标题
    private String title;
    //原因
    private String bz;

    //0未删除，1已删除
    private Integer isDelete;
    //保修时间
    private Date bxTime;
    //结束时间
    private Date endTime;
    //设备位置
    private String wz;
    private Equipment equipment;

    public void export() throws IOException {
        List<RepairLog> list = repairLogService.getList(repairLog).getDatas();
        // 生成Workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        // 追加Sheet
        Sheet sheet = wb.createSheet("Sheet");
        // 总列数
        Integer CountColumnNum = 6;
        Cell[] firstCell = new Cell[CountColumnNum];
        String[] firstCellNames = new String[CountColumnNum];
        firstCellNames[0] = "设备型号";
        firstCellNames[1] = "标题";
        firstCellNames[2] = "保修时间";
        firstCellNames[3] = "保修结束时间";
        firstCellNames[4] = "设备位置";
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
            zzs.setCellValue(list.get(i).getTitle().toString());

            Cell xh = row.createCell(2);
            xh.setCellValue(list.get(i).getBxTime().toString());
            Cell gh = row.createCell(3);
            gh.setCellValue(list.get(i).getEndTime().toString());
            Cell t = row.createCell(4);
            t.setCellValue(list.get(i).getWz().toString());
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

    /**
     * 维修
     *
     * @return
     */
    public void repair() throws IOException {
        Equipment equipment = equipmentService.findById(equipmentId);
        equipment.setIsBx(1);
        repairLog.setBxTime(new Date());
        repairLogService.save(repairLog);
        equipment.setBxLogid(repairLog.getId());
        equipmentService.updates(equipment);
        map.put("flag", true);
        map.put("url", "repair_repairlist.do");
        JsonUtils.toJson(map);
    }

    /**
     * 恢复使用
     *
     * @return
     */
    public void repairHf() throws IOException {
        Equipment equipment = equipmentService.findById(equipmentId);
        equipment.setIsBx(0);
        equipmentService.updates(equipment);
        RepairLog repairLog =repairLogService.findById(equipment.getBxLogid());
        repairLog.setEndTime(new Date());
        repairLogService.updates(repairLog);
        map.put("flag", true);
        map.put("url", "repair_repairlist.do");
        JsonUtils.toJson(map);
    }


    @Override
    public RepairLog getModel() {
        if (repairLog == null) {
            repairLog = new RepairLog();
        }
        return repairLog;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public RepairLog getRepairLog() {
        return repairLog;
    }

    public void setRepairLog(RepairLog repairLog) {
        this.repairLog = repairLog;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }
}
