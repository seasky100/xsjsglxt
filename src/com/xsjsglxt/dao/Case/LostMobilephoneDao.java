package com.xsjsglxt.dao.Case;

import java.util.List;

import com.xsjsglxt.domain.DO.xsjsglxt_case;
import com.xsjsglxt.domain.DO.xsjsglxt_lost_mobilephone;
import com.xsjsglxt.domain.DO.xsjsglxt_snece;
import com.xsjsglxt.domain.VO.Case.page_list_CasematerialVO;

public interface LostMobilephoneDao {

public 	void saveLostMobilephone(xsjsglxt_lost_mobilephone lost_mobilephone);

public int getCountLostMobilephoneInformationByPage(page_list_CasematerialVO page_list_Casematerial);

public List<xsjsglxt_lost_mobilephone> getListLostMobilephoneInformatioByPage(
		page_list_CasematerialVO page_list_Casematerial);

public xsjsglxt_case getCaseByxsjsglxt_lost_mobilephone_id(xsjsglxt_lost_mobilephone lost_mobilephone);

public xsjsglxt_snece getSenceByxsjsglxt_case_id(xsjsglxt_case case1);

public xsjsglxt_lost_mobilephone getLostMobiephoneById(xsjsglxt_lost_mobilephone lost_mobilephone);
/*
 * 修改损失手机信息
 */
public void updateParallel(xsjsglxt_lost_mobilephone lost_mobilephone);

public com.xsjsglxt.domain.DO.xsjsglxt_lost_mobilephone getLost_mobilephoneByNum(String lost_mobilephone_id);

public boolean deleteLost_mobilephoneById(String xsjsglxt_lost_mobilephone_id);

}
