package com.shineyue.pt.websocket;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindResultBean;
import com.shineyue.pt.comment.service.PtCommentEquipmentBindService;

/**
 * @author tianyunpeng
 * @create 2020-08-03 14:25
 */
@Component
@ServerEndpoint("/appSignWebSocket/{operatorId}")
public class AppSignWebSocket {
	static PtCommentEquipmentBindService service;
	public final static String SEND_MSG_RESULT_TYPE_SUCCESS = "1"; // 发送消息成功
	public final static String SEND_MSG_RESULT_TYPE_FAIL = "0"; // 发送消息失败

	public final static String MSG_TYPE_RESPONSE = "0";
	public final static String MSG_TYPE_PUSH = "1";
	public final static String MSG_TYPE_REPEAT_LOGIN = "2";
	public final static String MSG_TYPE_OPEN_COMMENT = "3"; // PC发消息，平板收到后打开评价面板
	public final static String MSG_TYPE_AUTO_OUT = "5"; // PC退出登录后，平板自动断开连接
	public final static String MSG_TYPE_GET_LOGIN_INFO = "6"; // 平板给PC发消息获取登录信息
	public final static String MSG_TYPE_AUTO_LOGIN = "8"; // PC给平板发消息自动登录
	public final static String MSG_TYPE_HEARTBEAT = "99"; // 返回心跳响应
	public final static String MSG_TYPE_ERROR = "500";
	private static Logger log = LoggerFactory.getLogger(AppSignWebSocket.class);
	/**
	 * 与某个客户端的连接对话，需要通过它来给客户端发送消息
	 */
	private Session session;

	/**
	 * 标识当前连接客户端的操作员id
	 */
	private String operatorId;

	/**
	 * 标识当前连接客户端的操作员userid
	 */
	private String userid;

	/**
	 * 标识当前连接客户端的操作员blqd
	 */
	private String blqd;

	/**
	 * 标识当前连接客户端的操作员zxbm
	 */
	private String zxbm;

	/**
	 * 标识当前连接客户端的操作员grbh
	 */
	private String grbh;
	public static ConcurrentHashMap<String, AppSignWebSocket> webSocketSet = new ConcurrentHashMap();
	public static ConcurrentHashMap<String, Date> statusManager = new ConcurrentHashMap();

	@Autowired
	public void setPtEquipmentBindService(PtCommentEquipmentBindService ptEquipmentBindService) {
		service = ptEquipmentBindService;
	}

	@PostConstruct
	public void init() {
		log.info("定时程序启动,心跳监听开始");
		Timer timer = new Timer();
		HeartbeatTask task = new HeartbeatTask();
		// 表示在0秒之后开始执行，并且每45秒执行一次
		timer.schedule(task, 0, 45000);
	}

	@OnOpen
	public void OnOpen(Session session, @PathParam("operatorId") String operatorId) throws InterruptedException {
		log.info("OnOpen接口调用当前websocket连接数量为={}", Integer.valueOf(webSocketSet.size()));
		this.session = session;
		this.operatorId = operatorId;
		System.out.println("用户登录" + operatorId);
		statusManager.put(operatorId, new Date());
		webSocketSet.put(operatorId, this);
		log.info("操作员ID为" + operatorId + " 连接成功，当前连接人数为：={}", Integer.valueOf(webSocketSet.size()));
	}

	@OnClose
	public void OnClose() {

		webSocketSet.remove(this.operatorId);

		// 如果是PC端断开连接，则签字板和叫号板自动断开连接
		PtCommentEquipmentBindBean requestBody = new PtCommentEquipmentBindBean();
		requestBody.setPcMacUrl(this.operatorId);
		try {
			List resultList = service.queryEquipmentBindList2(requestBody);
			log.info("查询设备绑定列表结果===" + resultList.toString());
			if (resultList.size() > 0) {
				PtCommentEquipmentBindResultBean equipment = (PtCommentEquipmentBindResultBean) resultList.get(0);
				String recipientId = equipment.getSignPadMacUrl();
				String recipientId2 = equipment.getCallPadMacUrl();
				JSONObject messageResult = new JSONObject();
				messageResult.put("msgType", MSG_TYPE_AUTO_OUT);
				try {
					AppointSending(recipientId, messageResult.toJSONString());
				} catch (Exception e) {
					log.error("向签字平板发送断开连接失败！");
				}
				try {
					AppointSending(recipientId2, messageResult.toJSONString());
				} catch (Exception e) {
					log.error("向叫号平板平板发送断开连接失败！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("操作员ID为 " + this.operatorId + "退出成功，当前连接人数为：={}", Integer.valueOf(webSocketSet.size()));
	}

	@OnMessage
	public String OnMessage(String message) {
		log.info("[WebSocket] 收到消息：{}", message);
		JSONObject jo = new JSONObject();
		jo = JSONObject.parseObject(message);
		JSONObject messageResult = new JSONObject();

		if (jo.containsKey("a")) {
			log.info("收到id为" + this.operatorId + "发来的心跳消息，刷新心跳时间");
			statusManager.put(this.operatorId, new Date());
			messageResult.put("msgType", "99");
			// webSocketSet.put(this.operatorId, this);
		} else if (jo.containsKey("recipientId")) {
			String recipientId = jo.getString("recipientId");
			try {
				String msgType = "1";

				if (jo.containsKey("msgType")) {
					msgType = jo.get("msgType").toString();
				}

				log.info("签字板websocket转发消息");
				jo.put("msgType", msgType);
				AppointSending(recipientId, jo.toJSONString());
			} catch (Exception e) {
				messageResult.put("msgType", "0");
				messageResult.put("type", "0");
				messageResult.put("message", e.getMessage());
			}
		}

		// pc登录后发送msgType=8，然后签字板自动登录
		if (jo.containsKey("msgType") && MSG_TYPE_AUTO_LOGIN.equals(jo.getString("msgType"))) {
			// this.zxbm = jo.getString("zxbm");
			// 查询PC绑定的平板mac地址
			PtCommentEquipmentBindBean ptEquipmentBindBean = new PtCommentEquipmentBindBean();
			ptEquipmentBindBean.setPcMacUrl(this.operatorId);
			try {
				List resultList = service.queryEquipmentBindList2(ptEquipmentBindBean);

				log.info("查询设备绑定列表结果===" + resultList.toString());
				if (resultList.size() > 0) {
					PtCommentEquipmentBindResultBean equipment = (PtCommentEquipmentBindResultBean) resultList.get(0);
					String signPadMacUrl = equipment.getSignPadMacUrl();
					String callPadMacUrl = equipment.getCallPadMacUrl();
					try {
						AppointSending(signPadMacUrl, jo.toString());
					} catch (Exception e) {
						log.error("向签字平板发送自动登录消息失败！");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if ((jo.containsKey("msgType")) && ("3".equals(jo.getString("msgType"))) && (!jo.containsKey("recipientId"))) {
			PtCommentEquipmentBindBean ptEquipmentBindBean = new PtCommentEquipmentBindBean();
			ptEquipmentBindBean.setPcMacUrl(this.operatorId);
			try {
				List resultList = service.queryEquipmentBindList2(ptEquipmentBindBean);

				log.info("查询设备绑定列表结果===" + resultList.toString());
				if (resultList.size() > 0) {
					PtCommentEquipmentBindResultBean equipment = (PtCommentEquipmentBindResultBean) resultList.get(0);
					String signPadMacUrl = equipment.getSignPadMacUrl();
					try {
						AppointSending(signPadMacUrl, jo.toString());
					} catch (Exception e) {
						log.error("向签字平板发送消息打开评价页面失败！");
						messageResult.put("msgType", MSG_TYPE_RESPONSE);
						messageResult.put("type", SEND_MSG_RESULT_TYPE_FAIL);
						messageResult.put("message", e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if ((jo.containsKey("msgType")) && ("6".equals(jo.getString("msgType"))) && (!jo.containsKey("recipientId"))) {
			PtCommentEquipmentBindBean ptEquipmentBindBean = new PtCommentEquipmentBindBean();
			ptEquipmentBindBean.setSignPadMacUrl(this.operatorId);
			try {
				List resultList = service.queryEquipmentBindList2(ptEquipmentBindBean);

				log.info("查询设备绑定列表结果===" + resultList.toString());
				if (resultList.size() > 0) {
					PtCommentEquipmentBindResultBean equipment = (PtCommentEquipmentBindResultBean) resultList.get(0);
					String pcMacUrl = equipment.getPcMacUrl();
					try {
						AppointSending(pcMacUrl, jo.toString());
					} catch (Exception e) {
						log.error("签字板请求PC，获取登录信息失败！");
						messageResult.put("msgType", MSG_TYPE_RESPONSE);
						messageResult.put("type", SEND_MSG_RESULT_TYPE_FAIL);
						messageResult.put("message", e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if ((jo.containsKey("senderId")) && (jo.getString("senderId").equals(this.operatorId))) {
			try {
				messageResult.put("msgType", "0");
				messageResult.put("type", "1");
				messageResult.put("message", "发送成功");
				// AppointSending(this.operatorId,
				// messageResult.toJSONString());
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return messageResult.toJSONString();
	}

	@OnError
	public void onError(Session session, Throwable error) {
		log.info("操作员ID为：{}的连接发送错误", this.operatorId);

		PtCommentEquipmentBindBean ptEquipmentBindBean = new PtCommentEquipmentBindBean();
		ptEquipmentBindBean.setPcMacUrl(this.operatorId);
		try {
			List resultList = service.queryEquipmentBindList2(ptEquipmentBindBean);
			log.info("查询设备绑定列表结果===" + resultList.toString());
			if (resultList.size() > 0) {
				PtCommentEquipmentBindResultBean equipment = (PtCommentEquipmentBindResultBean) resultList.get(0);
				String signPadMacUrl = equipment.getSignPadMacUrl();
				String callPadMacUrl = equipment.getCallPadMacUrl();
				JSONObject messageResult = new JSONObject();
				messageResult.put("msgType", "5");
				try {
					AppointSending(signPadMacUrl, messageResult.toJSONString());
				} catch (Exception e) {
					log.error("向签字平板发送断开连接失败！");
				}
				try {
					AppointSending(callPadMacUrl, messageResult.toJSONString());
				} catch (Exception e) {
					log.error("向叫号平板平板发送断开连接失败！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject result = new JSONObject();
		result.put("message", "websocket异常，连接已断开");
		result.put("msgType", "500");
		result.put("recipientId", this.operatorId);
		OnMessage(result.toString());
		webSocketSet.remove(this.operatorId);
		log.info("当前连接数为=={}", Integer.valueOf(webSocketSet.size()));
	}

	public void GroupSending(String message) {
		for (String name : webSocketSet.keySet())
			try {
				((AppSignWebSocket) webSocketSet.get(name)).session.getBasicRemote().sendText(message);
			} catch (Exception e) {
				log.error("群发失败！", e);
			}
	}

	public void AppointSending(String operatorId, String msg) throws Exception {
		try {
			log.info("签字板websocket向id为==={}的设备发送消息==={}", operatorId, msg);
			((AppSignWebSocket) webSocketSet.get(operatorId)).session.getBasicRemote().sendText(msg);
		} catch (Exception e) {
			String error = "id为" + operatorId + "的websocket连接不存在！";
			log.error(error);
			throw new Exception(error);
		}
	}
}