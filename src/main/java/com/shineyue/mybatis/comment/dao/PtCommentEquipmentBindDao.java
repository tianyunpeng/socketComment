package com.shineyue.mybatis.comment.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.shineyue.comment.bean.PtCommentEquipmentBindBean;
import com.shineyue.comment.bean.PtCommentEquipmentBindResultBean;

@Mapper
/**
 * 设备绑定
 * 
 * @author tianyunpeng
 * @date 2020年11月9日 10点08分
 */
public interface PtCommentEquipmentBindDao {

	/**
	 * 查询设备绑定列表
	 * 
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@Select({ "<script> select * from pt_equipment_bind where 1=1 "
			+ " <if test='zxbm!=null and zxbm!=\"\"'> and jgbm = #{zxbm} </if>"
			+ " <if test='pcMacUrl!=null and pcMacUrl!=\"\"'> and pcMacUrl = #{pcMacUrl} </if>"
			+ " <if test='callPadMacUrl!=null and callPadMacUrl!=\"\"'> and callPadMacUrl = #{callPadMacUrl} </if>"
			+ " <if test='signPadMacUrl!=null and signPadMacUrl!=\"\"'> and signPadMacUrl = #{signPadMacUrl} </if>"
			+ " <if test='bindUserId!=null and bindUserId!=\"\"'> and bindUserId = #{bindUserId} </if>"
			+ " <if test='fwwd!=null and fwwd!=\"\"'> and fwwd = #{fwwd} </if>"
			+ " <if test='ckh!=null and ckh!=\"\"'> and ckh like CONCAT('%',#{ckh},'%') </if>"
			+ " order by bindTime desc </script>" })
	public List<PtCommentEquipmentBindResultBean> queryEquipmentBindList(PtCommentEquipmentBindBean bean);

	/**
	 * 新增设备绑定
	 * 
	 * @author tianyunpeng
	 * @param bean
	 */
	@Insert({ " insert into pt_equipment_bind(id, callPadMacUrl, bindUserId, bindTime, "
			+ " signPadMacUrl, pcMacUrl, jgbm, fwwd, ckh) "
			+ " values(f_newid(), #{callPadMacUrl}, 'admin', sysdate(), #{signPadMacUrl},"
			+ " #{pcMacUrl}, 'admin', #{fwwd}, #{ckh})" })
	public void insertEquipmentBind(PtCommentEquipmentBindBean bean);

	/**
	 * 删除设备绑定
	 * 
	 * @author tianyunpeng
	 * @param bean
	 */
	@Delete({ "delete from pt_equipment_bind where id=#{id} " })
	public void deleteEquipmentBindById(PtCommentEquipmentBindBean bean);

	/**
	 * 修改设备绑定
	 * 
	 * @author tianyunpeng
	 * @param bean
	 */
	@Update({ "<script> update pt_equipment_bind set bindUserId = 'admin' "
			+ " <if test='callPadMacUrl!=null and callPadMacUrl!=\"\"'> , callPadMacUrl = #{callPadMacUrl} </if>"
			+ " <if test='signPadMacUrl!=null and signPadMacUrl!=\"\"'> , signPadMacUrl = #{signPadMacUrl} </if>"
			+ " <if test='pcMacUrl!=null and pcMacUrl!=\"\"'> , pcMacUrl = #{pcMacUrl} ,</if>"
			+ " where id=#{id} </script>" })
	public void updateEquipmentBindById(PtCommentEquipmentBindBean bean);

	/**
	 * 查询PC设备是否被绑定
	 * 
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@Select({ "<script> select fwwd,ckh from pt_equipment_bind where pcMacUrl = #{pcMacUrl} </script>" })
	public List<PtCommentEquipmentBindResultBean> queryEquipmentBindListByPcMacUrl(String pcMacUrl);

	/**
	 * 查询叫号设备是否被绑定
	 * 
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@Select({ "<script> select fwwd,ckh from pt_equipment_bind where callPadMacUrl = #{callPadMacUrl} </script>" })
	public List<PtCommentEquipmentBindResultBean> queryEquipmentBindListByCallMacUrl(String callPadMacUrl);

	/**
	 * 查询签字板设备是否被绑定
	 * 
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@Select({ "<script> select fwwd,ckh from pt_equipment_bind where signPadMacUrl = #{signPadMacUrl} </script>" })
	public List<PtCommentEquipmentBindResultBean> queryEquipmentBindListBySignMacUrl(String signPadMacUrl);

	/**
	 * 查询该网点和窗口是否已存在
	 * 
	 * @author tianyunpeng
	 * @param bean
	 * @return
	 */
	@Select({ "<script> select 1 from pt_equipment_bind where fwwd = #{fwwd} and ckh = #{ckh} </script>" })
	public Integer queryIsEquipmentBindByFwwdCkh(PtCommentEquipmentBindBean bean);

}
