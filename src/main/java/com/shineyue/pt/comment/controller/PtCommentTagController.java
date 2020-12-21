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

import com.alibaba.fastjson.JSONObject;
import com.shineyue.calldb.util.bean.DataResult;
import com.shineyue.comment.bean.PtCommentTagBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.pt.comment.service.PtCommentTagService;
import com.shineyue.pt.comment.service.PtCommentEquipmentBindService;

import lombok.extern.slf4j.Slf4j;

/**
 * 评价标签
 * @author tianyunpeng
 */
@RestController
@Slf4j
public class PtCommentTagController {
	
	@Autowired
	private PtCommentTagService service;
	
	/**
	 * 查询评价标签列表
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/ptCommentTag$m=query.service", method = RequestMethod.POST)
	@ResponseBody
	public DataResult queryCommentTagList(@RequestBody PtCommentTagBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			t = service.queryCommentTagList(bean);
		} catch (Exception e) {
			t.setSuccess(false);
			e.printStackTrace();
			trace = "查询评价标签列表失败：" + e.getMessage();
			t.setMsg("查询评价标签列表失败");
			log.error(trace);
		}
		return t;
	}
	
	/**
	 * 向签字板发送消息，打开评价页面
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/PT/business/comment/openComment.service", method = RequestMethod.POST)
	@ResponseBody
	public DataResult openComment(@RequestBody JSONObject bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			t = service.openComment(bean);
		} catch (Exception e) {
			t.setSuccess(false);
			e.printStackTrace();
			trace = "向签字板发送消息，打开评价页面失败：" + e.getMessage();
			t.setMsg("向签字板发送消息，打开评价页面失败");
			log.error(trace);
		}
		return t;
	}
	
	
	
}
