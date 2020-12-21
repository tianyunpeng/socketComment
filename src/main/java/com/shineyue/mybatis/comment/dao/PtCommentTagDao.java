package com.shineyue.mybatis.comment.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shineyue.comment.bean.PtCommentTagBean;
import com.shineyue.comment.bean.PtCommentTagResultBean;

import java.util.List;

@Mapper
/**
 * 评价标签
 * @author tianyunpeng
 * @date 2020年11月9日 10点08分
 */
public interface PtCommentTagDao {
	
	/**
	 * 查询评价标签列表
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@Select({ "<script> select * from pt_comment_tag </script>"})
	public List<PtCommentTagResultBean> queryCommentTagList(PtCommentTagBean bean);
}
