package com.shineyue.pt.comment.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SLF4JLogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shineyue.calldb.util.bean.DataResult;
import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindResultBean;
import com.shineyue.mybatis.comment.dao.PtCommentEquipmentBindDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 设备绑定管理
 * 
 * @author tianyunpeng
 */
@Service
@Slf4j
public class PtCommentEquipmentBindService {

	@Autowired
	private PtCommentEquipmentBindDao dao;
	@Autowired
	private ApplicationContext context;
	private static final Log log = SLF4JLogFactory.getLog(PtCommentEquipmentBindService.class);

	/**
	 * 查询设备绑定列表
	 */
	public DataResult queryEquipmentBindList(PtCommentEquipmentBindBean bean) {
		log.info("查询设备绑定列表入参" + bean.toString());
		DataResult t = new DataResult();
		String trace = "";
		try {
			PageHelper.startPage(bean.getPage(), bean.getSize());
			List<PtCommentEquipmentBindResultBean> list = dao.queryEquipmentBindList(bean);
			PageInfo<PtCommentEquipmentBindResultBean> pageInfo = new PageInfo<>(list, 5);
			int num = (int) pageInfo.getTotal();
			t.setTotalcount(num);
			t.setResults(list);
			t.setSuccess(true);
			t.setMsg("查询设备绑定列表成功！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("查询设备绑定列表失败！");
			e.printStackTrace();
			trace = "查询设备绑定列表失败:" + e.getMessage();
			log.error(trace);
		}
		return t;
	}

	/**
	 * 查询设备绑定列表 2
	 */
	public List<PtCommentEquipmentBindResultBean> queryEquipmentBindList2(PtCommentEquipmentBindBean bean) {
		log.info("查询设备绑定列表入参" + bean.toString());
		DataResult t = new DataResult();
		String trace = "";
		List<PtCommentEquipmentBindResultBean> list = new ArrayList<PtCommentEquipmentBindResultBean>();
		try {
			PageHelper.startPage(bean.getPage(), bean.getSize());
			list = dao.queryEquipmentBindList(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 新增设备绑定
	 */
	public DataResult insertEquipmentBind(PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			// 查询设备是否被已被绑定
			DataResult bindResult = new DataResult();
			bindResult = queryEquipmentBindListByMacUrl(bean);
			if (!bindResult.isSuccess()) {
				return bindResult;
			}
			// 查询该服务网点下是否已存在该窗口号
			Integer isCkh = dao.queryIsEquipmentBindByFwwdCkh(bean);
			if (null != isCkh) {
				t.setSuccess(false);
				t.setMsg("同一服务网点下，窗口不能重复！");
				return t;
			}
			dao.insertEquipmentBind(bean);
			t.setSuccess(true);
			t.setMsg("新增设备绑定成功！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("新增设备绑定失败！");
			e.printStackTrace();
			trace = "新增设备绑定失败:" + e.getMessage();
			log.error(trace);
		}
		return t;
	}

	/**
	 * 删除设备绑定
	 */
	public DataResult deleteEquipmentBindById(PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			dao.deleteEquipmentBindById(bean);
			t.setSuccess(true);
			t.setMsg("删除设备绑定成功！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("删除设备绑定失败！");
			e.printStackTrace();
			trace = "删除设备绑定失败:" + e.getMessage();
			log.error(trace);
		}
		return t;
	}

	/**
	 * 修改设备绑定
	 */
	public DataResult updateEquipmentBindById(PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			// 查询要修改的mac地址是否已被使用
			DataResult bindResult = new DataResult();
			if (null != bean.getPcMacUrl()) {
				List<PtCommentEquipmentBindResultBean> pcList = dao
						.queryEquipmentBindListByPcMacUrl(bean.getPcMacUrl());
				if (pcList.size() > 0) {
					t.setSuccess(false);
					String errorStr = "修改设备绑定失败！该PC已经在" + pcList.get(0).getFwwd() + "服务网点的" + pcList.get(0).getCkh()
							+ "窗口号绑定,不可重复新增,仅可修改、删除。";
					t.setMsg(errorStr);
					return t;
				}
			}
			if (null != bean.getCallPadMacUrl()) {
				List<PtCommentEquipmentBindResultBean> callList = dao
						.queryEquipmentBindListByCallMacUrl(bean.getCallPadMacUrl());
				if (callList.size() > 0) {
					t.setSuccess(false);
					String errorStr = "修改设备绑定失败！该叫号平板已经在" + callList.get(0).getFwwd() + "服务网点的"
							+ callList.get(0).getCkh() + "窗口号绑定,不可重复新增,仅可修改、删除。";
					t.setMsg(errorStr);
					return t;
				}
			}
			if (null != bean.getSignPadMacUrl()) {
				List<PtCommentEquipmentBindResultBean> signList = dao
						.queryEquipmentBindListBySignMacUrl(bean.getSignPadMacUrl());
				if (signList.size() > 0) {
					t.setSuccess(false);
					String errorStr = "修改设备绑定失败！该签字平板已经在" + signList.get(0).getFwwd() + "服务网点的"
							+ signList.get(0).getCkh() + "窗口号绑定,不可重复新增,仅可修改、删除。";
					t.setMsg(errorStr);
					return t;
				}
			}
			dao.updateEquipmentBindById(bean);
			t.setSuccess(true);
			t.setMsg("修改设备绑定成功！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("修改设备绑定失败！");
			e.printStackTrace();
			trace = "修改设备绑定失败:" + e.getMessage();
			log.error(trace);
		}
		return t;
	}

	/**
	 * 查询设备是否被已被绑定
	 */
	public DataResult queryEquipmentBindListByMacUrl(PtCommentEquipmentBindBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			// 先查询PC设备是否被绑定
			List<PtCommentEquipmentBindResultBean> list = dao.queryEquipmentBindListByPcMacUrl(bean.getPcMacUrl());
			if (list.size() > 0) {
				// 该PC已经在X服务网点的X窗口号绑定,不可重复新增,仅可修改、删除。
				t.setSuccess(false);
				String errorStr = "新增设备绑定失败！该PC已经在" + list.get(0).getFwwd() + "服务网点的" + list.get(0).getCkh()
						+ "窗口号绑定,不可重复新增,仅可修改、删除。";
				t.setMsg(errorStr);
				return t;
			} else {
				// 再查询叫号平板是否被绑定
				List<PtCommentEquipmentBindResultBean> callList = dao
						.queryEquipmentBindListByCallMacUrl(bean.getCallPadMacUrl());
				if (callList.size() > 0) {
					// 该叫号平板已经在X服务网点的X窗口号绑定,不可重复新增,仅可修改、删除。
					t.setSuccess(false);
					String errorStr = "新增设备绑定失败！该叫号平板已经在" + callList.get(0).getFwwd() + "服务网点的"
							+ callList.get(0).getCkh() + "窗口号绑定,不可重复新增,仅可修改、删除。";
					t.setMsg(errorStr);
					return t;
				} else {
					// 再查询签字板是否被绑定
					List<PtCommentEquipmentBindResultBean> signList = dao
							.queryEquipmentBindListBySignMacUrl(bean.getSignPadMacUrl());
					if (signList.size() > 0) {
						// 该叫号平板已经在X服务网点的X窗口号绑定,不可重复新增,仅可修改、删除。
						t.setSuccess(false);
						String errorStr = "新增设备绑定失败！该签字平板已经在" + signList.get(0).getFwwd() + "服务网点的"
								+ signList.get(0).getCkh() + "窗口号绑定,不可重复新增,仅可修改、删除。";
						t.setMsg(errorStr);
						return t;
					}
				}
			}
			t.setSuccess(true);
			t.setMsg("设备无绑定，可新增、修改！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("查询设备是否被已被绑定失败！");
			e.printStackTrace();
			trace = "查询设备是否被已被绑定失败:" + e.getMessage();
			log.error(trace);
		}
		return t;
	}

	public DataResult exportPtEquipmentBindData(PtCommentEquipmentBindBean bean) {
		DataResult dataResult = new DataResult();
		List<PtCommentEquipmentBindResultBean> results = new ArrayList<PtCommentEquipmentBindResultBean>();
		try {
			PageHelper.startPage(bean.getPage(), bean.getSize());
			results = dao.queryEquipmentBindList(bean);
			String fileName = exportExcel(results);
			log.info("导出设备绑定信息结果====>导出条数" + results.size() + ",文件名==>" + fileName);
			dataResult.setSuccess(true);
			dataResult.setMsg(fileName);
		} catch (Exception e) {
			log.info("设备绑定信息导出异常===>", e);
			dataResult.setMsg("设备绑定信息导出失败 " + e.getMessage());
		}
		return dataResult;
	}

	/**
	 * 导出设备绑定信息 excel表
	 * 
	 * @param lis
	 * @return
	 */
	public String exportExcel(List<PtCommentEquipmentBindResultBean> lis) {
		String name = "os.name";
		String linux = "linux";
		String linuxu = "Linux";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		String fileName = "d:/export_test/设备绑定信息" + df.format(new Date()) + ".xlsx";
		if (System.getProperty(name).toLowerCase().startsWith(linux)
				|| System.getProperty(name).toLowerCase().startsWith(linuxu)) {
			fileName = "/home/temp/" + df.format(new Date()) + ".xlsx";
		}
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				log.info("创建文件失败==>", e);
			}
		}
		file.setWritable(true, false);
		SXSSFWorkbook xWorkbook = null;
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(new File(fileName));
			if (lis == null || lis.size() == 0) {
				xWorkbook = new SXSSFWorkbook(2000);
				Sheet sh = xWorkbook.createSheet("Sheet" + 1);
				setSheetHeader(xWorkbook, sh);
			}
			if (lis != null && lis.size() > 0) {
				int rowaccess = 2000;
				int totle = lis.size();
				int mus = 40000;
				int avg = totle / mus;
				if (totle % mus > 0) {
					avg = avg + 1;
				}
				xWorkbook = new SXSSFWorkbook(rowaccess);
				CellStyle cs = xWorkbook.createCellStyle();
				setSheet(xWorkbook, lis, rowaccess, totle, mus, avg, cs);
			}
			xWorkbook.write(output);
		} catch (Exception e) {
			fileName = "设备绑定信息导出异常!";
			log.info(fileName, e);
		} finally {
			IOUtils.closeQuietly(output);
		}
		return fileName;
	}

	private void setSheetHeader(SXSSFWorkbook xWorkbook, Sheet sh) {
		org.apache.poi.ss.usermodel.Row xRow0 = sh.createRow(0);
		Cell xCell0 = xRow0.createCell(0);
		xCell0.setCellValue("序号");
		Cell xCell1 = xRow0.createCell(1);
		xCell1.setCellValue("服务网点");
		Cell xCell2 = xRow0.createCell(2);
		xCell2.setCellValue("窗口号");
		Cell xCell3 = xRow0.createCell(3);
		xCell3.setCellValue("电脑mac");
		Cell xCell4 = xRow0.createCell(4);
		xCell4.setCellValue("叫号板mac");
		Cell xCell5 = xRow0.createCell(5);
		xCell5.setCellValue("签字板mac");
	}

	/**
	 * 
	 * @param xWorkbook
	 * @param lis
	 * @param rowaccess
	 * @param totle
	 * @param mus
	 * @param avg
	 * @param cs
	 * @throws IOException
	 */
	private void setSheet(SXSSFWorkbook xWorkbook, List<PtCommentEquipmentBindResultBean> lis, int rowaccess, int totle,
			int mus, int avg, CellStyle cs) throws IOException {
		int totcnt = 49;
		for (int i = 0; i < avg; i++) {
			Sheet sh = xWorkbook.createSheet("Sheet" + (i + 1));
			setSheetHeader(xWorkbook, sh);
			int num = i * mus;
			int index = 0;
			for (int m = num; m < totle; m++) {
				if (index == mus) {
					break;
				}
				org.apache.poi.ss.usermodel.Row xRow = sh.createRow(m + 1 - mus * i);
				PtCommentEquipmentBindResultBean emp = (PtCommentEquipmentBindResultBean) lis.get(m);
				for (int j = 0; j < totcnt; j++) {
					Cell xCell = xRow.createCell(j);
					cs.setWrapText(true);
					setCellValue(m, j, xCell, emp);
				}
				index++;
				if (m % rowaccess == 0) {
					((SXSSFSheet) sh).flushRows();
				}
			}
		}
	}

	/**
	 * @declare 查询导出
	 * @author 田云鹏
	 * @version 2020年11月11日 10点45分
	 * @param lis
	 * @return
	 */
	private void setCellValue(int m, int j, Cell xCell, PtCommentEquipmentBindResultBean emp) {
		switch (j) {
		case 0:
			/**
			 * 序号
			 */
			xCell.setCellValue((m + 1) + "");
			break;
		case 1:
			/**
			 * 服务网点
			 */
			xCell.setCellValue(emp.getFwwd());
			break;
		case 2:
			/**
			 * 窗口号
			 */
			xCell.setCellValue(emp.getCkh());
			break;
		case 3:
			/**
			 * 电脑mac
			 */
			xCell.setCellValue(emp.getPcMacUrl());
			break;
		case 4:
			/**
			 * 叫号板mac
			 */
			xCell.setCellValue(emp.getCallPadMacUrl());
			break;
		case 5:
			/**
			 * 签字板mac
			 */
			xCell.setCellValue(emp.getSignPadMacUrl());
			break;
		default:
			break;
		}
	}

}
