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
import com.shineyue.comment.bean.PtCommentTagBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.comment.bean.PtFwwdBean;
import com.shineyue.pt.comment.service.PtCommentTagService;
import com.shineyue.pt.comment.service.PtCommentEquipmentBindService;
import com.shineyue.pt.comment.service.PtFwwdService;

import lombok.extern.slf4j.Slf4j;

/**
 * 评价标签
 * @author tianyunpeng
 */
@RestController
@Slf4j
public class PtFwwdController {
	
	@Autowired
	private PtFwwdService service;
	
	/**
	 * 查询服务网点列表
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/ptFwwd$m=query.service", method = RequestMethod.POST)
	@ResponseBody
	public DataResult queryCommentTagList(@RequestBody PtFwwdBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			t = service.queryFwwdList(bean);
		} catch (Exception e) {
			t.setSuccess(false);
			e.printStackTrace();
			trace = "查询服务网点列表失败：" + e.getMessage();
			t.setMsg("查询服务网点列表失败");
			log.error(trace);
		}
		return t;
	}
	
}
