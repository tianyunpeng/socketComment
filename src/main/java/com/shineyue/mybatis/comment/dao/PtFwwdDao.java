package com.shineyue.mybatis.comment.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shineyue.comment.bean.PtCommentTagBean;
import com.shineyue.comment.bean.PtFwwdBean;
import com.shineyue.comment.bean.PtFwwdResultBean;

import java.util.List;

@Mapper
/**
 * 服务网点
 * @author tianyunpeng
 * @date 2020年11月9日 10点08分
 */
public interface PtFwwdDao {
	
	/**
	 * 查服务网点列表
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@Select({ "<script> select * from pt_fwwd </script>"})
	public List<PtFwwdResultBean> queryFwwdList(PtFwwdBean bean);
}
