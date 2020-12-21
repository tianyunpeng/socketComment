package com.shineyue.pt.comment.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shineyue.calldb.util.bean.DataResult;
import com.shineyue.comment.bean.PtCommentTagBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.comment.bean.PtFwwdBean;
import com.shineyue.comment.bean.PtFwwdResultBean;
import com.shineyue.mybatis.comment.dao.PtCommentTagDao;
import com.shineyue.mybatis.comment.dao.PtCommentEquipmentBindDao;
import com.shineyue.mybatis.comment.dao.PtFwwdDao;
import com.shineyue.pt.common.utils.FastDfsClientUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SLF4JLogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * 服务网点
 * @author tianyunpeng
 */
@Service
@Slf4j
public class PtFwwdService {
	
	@Autowired
	private PtFwwdDao dao;
	@Autowired
	private ApplicationContext context;
	private static final Log log = SLF4JLogFactory.getLog(PtFwwdService.class);
	
	/**
	 * 查询服务网点列表
	 */
	public DataResult queryFwwdList(PtFwwdBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			PageHelper.startPage(bean.getPage(), bean.getSize());
			List<PtFwwdResultBean> list = dao.queryFwwdList(bean);
			PageInfo<PtFwwdResultBean> pageInfo = new PageInfo<>(list, 5);
			int num = (int) pageInfo.getTotal();
			t.setTotalcount(num);
			t.setResults(list);
			t.setSuccess(true);
			t.setMsg("查询服务网点列表成功！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("查询服务网点列表失败！");
			e.printStackTrace();
			trace = "查询服务网点列表失败:"+e.getMessage();
			log.error(trace);
		}
		return t;
	}
}
