package com.shineyue.comment.bean;

import org.springframework.beans.factory.annotation.Value;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
/**
 * @author tianyunpeng
 * @date 2020年11月9日 10点04分
 */
public class PtCommentEquipmentBindResultBean {

	private String id;
	// PC的mac地址
	private String pcMacUrl;
	// 叫号平板mac地址
	private String callPadMacUrl;
	// 签字板mac地址
	private String signPadMacUrl;
	// 绑定人id
	private String bindUserId;
	// 绑定时间
	private String bindTime;
	private String jgbm;
	private String zxbm;
	// 服务网点
	private String fwwd; 
	// 窗口号
	private String ckh; 
}
