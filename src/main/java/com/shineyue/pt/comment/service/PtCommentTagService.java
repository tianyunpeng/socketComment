package com.shineyue.pt.comment.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SLF4JLogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shineyue.calldb.util.bean.DataResult;
import com.shineyue.comment.bean.PtCommentTagBean;
import com.shineyue.comment.bean.PtCommentTagResultBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindResultBean;
import com.shineyue.mybatis.comment.dao.PtCommentTagDao;
import com.shineyue.pt.websocket.AppSignWebSocket;

import lombok.extern.slf4j.Slf4j;

/**
 * 评价标签
 * 
 * @author tianyunpeng
 */
@Service
@Slf4j
public class PtCommentTagService {
	@Autowired
	private PtCommentTagDao dao;
	@Autowired
	private AppSignWebSocket appSignWebSocket;
	@Autowired
	private PtCommentEquipmentBindService service;
	@Autowired
	private ApplicationContext context;
	private static final Log log = SLF4JLogFactory.getLog(PtCommentTagService.class);

	/**
	 * 查询评价标签列表
	 */
	public DataResult queryCommentTagList(PtCommentTagBean bean) {
		DataResult t = new DataResult();
		String trace = "";
		try {
			PageHelper.startPage(bean.getPage(), bean.getSize());
			List<PtCommentTagResultBean> list = dao.queryCommentTagList(bean);
			PageInfo<PtCommentTagResultBean> pageInfo = new PageInfo<>(list, 5);
			int num = (int) pageInfo.getTotal();
			t.setTotalcount(num);
			t.setResults(list);
			t.setSuccess(true);
			t.setMsg("查询评价标签列表成功！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("查询评价标签列表失败！");
			e.printStackTrace();
			trace = "查询评价标签列表失败:" + e.getMessage();
			log.error(trace);
		}
		return t;
	}

	/**
	 * 向签字板发送消息，打开评价页面
	 */
	public DataResult openComment(JSONObject bean) {
		DataResult t = new DataResult();
		String trace = "";
		PtCommentEquipmentBindBean requestBody = new PtCommentEquipmentBindBean();
		requestBody.setPcMacUrl(bean.getString("pcMacUrl"));
		try {
			List<PtCommentEquipmentBindResultBean> resultList = service.queryEquipmentBindList2(requestBody);
			log.info("查询设备绑定列表结果" + resultList.toString());
			if (resultList.size() > 0) {
				PtCommentEquipmentBindResultBean equipment = resultList.get(0);
				String signPadMacUrl = equipment.getSignPadMacUrl();
				appSignWebSocket.AppointSending(signPadMacUrl, bean.toString());
			}
			t.setSuccess(true);
			t.setMsg("向签字板发送消息，打开评价页面成功！");
		} catch (Exception e) {
			t.setSuccess(false);
			t.setMsg("向签字板发送消息，打开评价页面失败！");
			e.printStackTrace();
			trace = "向签字板发送消息，打开评价页面失败:" + e.getMessage();
			log.error(trace);
		}
		return t;
	}
}
