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
public class PtCommentTagResultBean {
	// 评价id
	private String tagId;
	// 评价内容
	private String tagName;
	// 评价星级
	private String starNum;
}
