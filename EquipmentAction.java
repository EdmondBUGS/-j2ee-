package com.xznu.edu.leave.action;

/**
 * 用户新增
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.xznu.edu.leave.model.*;
import com.xznu.edu.leave.service.*;
import com.xznu.edu.leave.utils.*;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("equipmentAction")
@Scope("prototype")
public class EquipmentAction extends ActionSupport implements ModelDriven<Equipment> {
    @Autowired
    private LaboratoryService laboratoryService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private EquipmentLogService equipmentLogService;
    @Autowired
    private RepairLogService repairLogService;
    private Equipment equipment;
    private Map<String, Object> map = new HashMap();
    private File file;
    private String fileFileName;
    private String fileContentType;
    private String downloadFileName;
    private Integer exportId;

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
        Pager<Equipment> pagers = null;
        Role role = user1.getRole();
        if (role.getEnName().equals("admin")) {
            pagers = equipmentService.getList(equipment);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            Pager<Laboratory> pagers2 = laboratoryService.getList(null);
            ActionContext.getContext().put("pagers2", pagers2);
            ActionContext.getContext().put("bean", equipment);
        }
        return SUCCESS;
    }

    /**
     * list
     *
     * @return
     */
    public String dataList() {
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null) {
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<Equipment> pagers = null;
        Role role = user1.getRole();
        if (role.getEnName().equals("admin")) {
            pagers = equipmentService.getList(equipment);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            Pager<Laboratory> pagers2 = laboratoryService.getList(null);
            ActionContext.getContext().put("pagers2", pagers2);
            ActionContext.getContext().put("bean", equipment);

        }
        return SUCCESS;
    }


    /**
     * list
     *
     * @return
     */
    public String dataList2() {
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null) {
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<Equipment> pagers = null;
        Role role = user1.getRole();
        if (role.getEnName().equals("admin")) {
            pagers = equipmentService.getList(equipment);
            ActionContext.getContext().put("pagers", pagers);
            ActionContext.getContext().put("user", user1);
            Pager<Laboratory> pagers2 = laboratoryService.getList(null);
            ActionContext.getContext().put("pagers2", pagers2);
            ActionContext.getContext().put("bean", equipment);
        }
        return SUCCESS;
    }

    /**
     * 跳转add
     *
     * @return
     */
    public String add() {
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
        Equipment bean = equipmentService.findById(equipment.getId());
        Pager<Laboratory> pagers2 = laboratoryService.getList(null);
        ActionContext.getContext().put("pagers2", pagers2);
        ActionContext.getContext().put("bean", bean);
        return SUCCESS;
    }


    /**
     * 使用
     *
     * @return
     */
    public void updateSh() throws IOException {
        equipment.setFwTime(new Date());
        equipment.setIsFw(1);
        equipmentService.updates(equipment);
        map.put("flag", true);
        map.put("url", "equipment_dataList.do");
        JsonUtils.toJson(map);
    }

    /**
     * 借用list
     *
     * @return
     */
    public String dataList3() {
        User user1 = UserUtils.getUser();
        if (user1 == null || user1.getId() == null) {
            ActionContext.getContext().put("login", 1);
            return SUCCESS;
        }
        Pager<Equipment> pagers = null;
        pagers = equipmentService.getList(equipment);
        ActionContext.getContext().put("pagers", pagers);
        ActionContext.getContext().put("user", user1);
        Pager<Laboratory> pagers2 = laboratoryService.getList(null);
        ActionContext.getContext().put("pagers2", pagers2);
        ActionContext.getContext().put("bean", equipment);
        return SUCCESS;
    }

    /**
     * 借用
     *
     * @return
     */
    public void yy() throws IOException {
        Equipment equipment1 = equipmentService.findById(equipment.getId());
        if (equipment1.getIsJy() == 0) {
            equipment.setIsJy(1);
            equipment.setJyUser(UserUtils.getUser());
            EquipmentLog log = new EquipmentLog();
            log.setUser(UserUtils.getUser());
            log.setTime(new Date());
            log.setEquipment(equipment);
            log.setIsYy(1);
            equipmentLogService.save(log);
            equipment.setJyId(log.getId());
            equipmentService.updates(equipment);
            map.put("flag", true);
            map.put("url", "equipment_dataList3.do");
            JsonUtils.toJson(map);
        } else {
            map.put("flag", false);
            map.put("url", "equipment_dataList3.do");
            JsonUtils.toJson(map);
        }

    }

    /**
     * 借用归还
     *
     * @return
     */
    public void qx() throws IOException {
        Equipment equipment1 = equipmentService.findById(equipment.getId());
        EquipmentLog equipmentLog = equipmentLogService.findById(equipment1.getJyId());
        if (equipmentLog.getUser().getId() == UserUtils.getUser().getId()) {
            equipment1.setIsJy(0);
            equipmentService.update(equipment1);
            equipmentLog.setIsYy(0);
            equipmentLog.setEndTime(new Date());
            equipmentLog.setQx(UserUtils.getUser());
            equipmentLogService.updates(equipmentLog);
            map.put("flag", true);
            map.put("url", "equipment_dataList3.do");
            JsonUtils.toJson(map);
        } else {
            map.put("flag", false);
            map.put("url", "equipment_dataList3.do");
            JsonUtils.toJson(map);
        }
    }

    /**
     * 更新
     *
     * @return
     */
    public String updateDoc() throws IOException {
        if (file != null) {
            ActionContext ac = ActionContext.getContext();
            HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
            String root = request.getRealPath("/upload");
            InputStream is = new FileInputStream(file);
            String f = fileFileName;
            f = UUIDUtils.create() + fileFileName;
            OutputStream os = new FileOutputStream(new File(root, f));
            byte[] buffer = new byte[500];
            int length = 0;
            while (-1 != (length = is.read(buffer, 0, buffer.length))) {
                os.write(buffer);
            }
            os.close();
            is.close();
            equipment.setHt("\\upload\\" + f);
        }
        equipmentService.updates(equipment);
        ActionContext.getContext().put("url", "equipment_dataList.do");
        return "redirect";
    }

    /**
     * 更新
     *
     * @return
     */
    public void deleteDocAndPhoto() throws IOException {
        Equipment ee = equipmentService.findById(equipment.getId());
        ee.setPhoto(null);
        ee.setHt(null);
        equipmentService.updates(equipment);
        map.put("flag", true);
        map.put("url", "equipment_dataList2.do");
        JsonUtils.toJson(map);
    }

    /**
     * 更新
     *
     * @return
     */
    public String updatePhoto() throws IOException {
        if (file != null) {
            ActionContext ac = ActionContext.getContext();
            HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
            String root = request.getRealPath("/upload");
            InputStream is = new FileInputStream(file);
            String f = fileFileName;
            f = UUIDUtils.create() + fileFileName;
            OutputStream os = new FileOutputStream(new File(root, f));
            byte[] buffer = new byte[500];
            int length = 0;
            while (-1 != (length = is.read(buffer, 0, buffer.length))) {
                os.write(buffer);
            }
            os.close();
            is.close();
            equipment.setPhoto("\\upload\\" + f);
        }
        equipmentService.updates(equipment);
        ActionContext.getContext().put("url", "equipment_dataList2.do");
        return "redirect";
    }

    /**
     * 更新
     *
     * @return
     */
    public void update() throws IOException {
        equipmentService.updates(equipment);
        map.put("flag", true);
        map.put("url", "equipment_dataList.do");
        JsonUtils.toJson(map);
    }

    public void download() throws IOException {
        Equipment equipment1 = equipmentService.findById(equipment.getId());
        downloadFileName = URLEncoder.encode(equipment1.getHt(), "utf-8");
        // 1、得到要下载文件的完整路径
        String path = ServletActionContext.getServletContext().getRealPath(equipment1.getHt());
        System.out.println("path :" + path);
        HttpServletResponse resp = ServletActionContext.getResponse();
        resp.setContentType("application/x-msdownload");// 指定响应动作是下载路径
        resp.setHeader("Content-disposition", "attachment;filename=export.doc");
        //读取文件
        InputStream in = new FileInputStream(path);
        OutputStream out = resp.getOutputStream();
        //写文件
        int b;
        while ((b = in.read()) != -1) {
            out.write(b);
        }
        in.close();
        out.close();
        map.put("flag", true);
        map.put("url", "equipment_dataList.do");
        JsonUtils.toJson(map);
    }


    /**
     * 保存
     *
     * @return
     */
    public String save() throws IOException {
        equipmentService.save(equipment);
        map.put("flag", true);
        map.put("url", "equipment_list.do");
        JsonUtils.toJson(map);
        return SUCCESS;
    }

    public void delete() throws IOException {
        equipment.setIsDelete(1);
        equipmentService.updates(equipment);
        map.put("flag", true);
        map.put("url", "equipment_list.do");
        JsonUtils.toJson(map);
    }

    public void export() throws IOException {
        List<Equipment> list = equipmentService.getList(equipment).getDatas();
        // 生成Workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        // 追加Sheet
        Sheet sheet = wb.createSheet("Sheet");
        // 总列数
        Integer CountColumnNum = 6;
        Cell[] firstCell = new Cell[CountColumnNum];
        String[] firstCellNames = new String[CountColumnNum];
        firstCellNames[0] = "设备型号";
        firstCellNames[1] = "设备价格";
        firstCellNames[2] = "设备制造商";
        firstCellNames[3] = "设备序列号";
        firstCellNames[4] = "服务开始时间";
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
            id.setCellValue(list.get(i).getXh().toString());
            bd = new BigDecimal(list.get(i).getJg().toString());
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            Cell by = row.createCell(1);
            by.setCellValue(bd.toString());
            Cell zzs = row.createCell(2);
            zzs.setCellValue(list.get(i).getZzs().toString());
            Cell xh = row.createCell(3);
            xh.setCellValue(list.get(i).getSbxlh().toString());
            Cell sj = row.createCell(4);
            if (list.get(i).getFwTime() != null && !list.get(i).getFwTime().equals("")) {
                sj.setCellValue(list.get(i).getFwTime().toString());
            }
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
    public Equipment getModel() {
        if (equipment == null) {
            equipment = new Equipment();
        }
        return equipment;
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

    public Integer getExportId() {
        return exportId;
    }

    public void setExportId(Integer exportId) {
        this.exportId = exportId;
    }
}
