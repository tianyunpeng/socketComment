package com.shineyue.pt.comment.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.shineyue.calldb.util.bean.DataResult;
import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.pt.comment.service.PtCommentEquipmentBindService;

import lombok.extern.slf4j.Slf4j;

/**
 * 设备绑定管理
 * @author tianyunpeng
 */
@RestController
@Slf4j
public class PtCommentEquipmentBindController {
	
	@Autowired
	private PtCommentEquipmentBindService service;
	
	/**
	 * 查询设备绑定列表
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/ptEquipmentBind$m=query.service", method = RequestMethod.POST)
	@ResponseBody
	public DataResult queryEquipmentBindList(@RequestBody PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			t = service.queryEquipmentBindList(bean);
		} catch (Exception e) {
			t.setSuccess(false);
			e.printStackTrace();
			trace = "查询设备绑定列表失败：" + e.getMessage();
			t.setMsg("查询设备绑定列表失败");
			log.error(trace);
		}
		return t;
	}
	
	/**
	 * 新增设备绑定
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/ptEquipmentBind$m=insert.service", method = RequestMethod.POST)
	@ResponseBody
	public DataResult insertEquipmentBind(@RequestBody PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		try {
			t = service.insertEquipmentBind(bean);
		} catch (Exception e) {
			log.info("新增我的按钮异常==>{}"+e);
			t.setSuccess(false);
			t.setMsg("新增我的按钮失败！");
		}
		return t;
	}
	/**
	 * 删除设备绑定
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/ptEquipmentBind$m=delete.service", method = RequestMethod.POST)
	@ResponseBody
	public DataResult deleteEquipmentBindById(@RequestBody PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		try {
			t = service.deleteEquipmentBindById(bean);
		} catch (Exception e) {
			log.info("删除设备绑定异常==>{}"+e);
			t.setSuccess(false);
			t.setMsg("删除设备绑定失败！");
		}
		return t;
	}
	/**
	 * 修改设备绑定
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/ptEquipmentBind$m=update.service", method = RequestMethod.POST)
	@ResponseBody
	public DataResult updateEquipmentBindById(@RequestBody PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		try {
			t = service.updateEquipmentBindById(bean);
		} catch (Exception e) {
			log.info("修改设备绑定异常==>{}"+e);
			t.setSuccess(false);
			t.setMsg("修改设备绑定失败！");
		}
		return t;
	}
	/**
	 * 导出设备绑定信息
	 * @param appSportBean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/exportPtEquipmentBindExcel.service", method = RequestMethod.POST )
	public void exportSportsExcel(@RequestBody PtCommentEquipmentBindBean bean,HttpServletResponse response) {
		log.info(" 导出设备绑定信息请求参数===>{}",bean);
		exportExcel(bean,response);
	} 
	
	private void  exportExcel(PtCommentEquipmentBindBean bean,HttpServletResponse response) {
		try {
			    DataResult result =  service.exportPtEquipmentBindData(bean);
				String url = result.getMsg().replace("\"", "");
				File file = new File(url);
				String filename = "设备绑定信息导出.xlsx";
				InputStream fis = new BufferedInputStream(new FileInputStream(url));
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();
				response.reset();
				response.setHeader("Content-Disposition",
						"attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("gbk"), "iso8859-1"));
				response.setHeader("Content-Length", "" + file.length());
				OutputStream os = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				os.write(buffer);
				os.flush();
				os.close();
			} catch (Exception e) {
				log.info("设备绑定信息导出异常",e);
			}
	}
}
