package com.shineyue.pt.websocket;

import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatTask extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(HeartbeatTask.class);

	public void run() {
		for (Map.Entry<String, Date> entry : AppSignWebSocket.statusManager.entrySet()) {
			System.out.println("当前连接id===" + entry.getKey() + "==心跳时间===" + entry.getValue());
			if (isBreak(entry.getValue())) {
				// 如果已断开，则移除
				log.info("id为" + entry.getKey() + "的连接已断开！移除此id的连接！");
				AppSignWebSocket.statusManager.remove(entry.getKey());
				AppSignWebSocket.webSocketSet.remove(entry.getKey());
			}
		}

	}

	public Boolean isBreak(Date startDate) {
		long nowTime = new Date().getTime();
		long heartBeatTime = startDate.getTime();
		int timeDifference = (int) ((nowTime - heartBeatTime) / 1000);
		if (timeDifference > 30) {
			// 连接已断开！
			return true;
		} else {
			return false;
		}
	}
}