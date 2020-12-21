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
public class PtFwwdBean {
	// 服务网点编码
	private String bm;
	// 服务网点名称
	private String mc;
	private int page;
	private int size;
}
