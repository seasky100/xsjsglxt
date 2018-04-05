package com.xsjsglxt.dao.Statistics;

import java.util.List;

import com.xsjsglxt.domain.DO.xsjsglxt_staff;
import com.xsjsglxt.domain.DTO.Statistics.policemanOutTimesDTO;
import com.xsjsglxt.domain.VO.Statistics.OutTimeVO;

public interface StatisticsDao {

	List<xsjsglxt_staff> getPolicemanByName(String policemanName);

	List<policemanOutTimesDTO> getTimes(List<xsjsglxt_staff> policeman, OutTimeVO outTimeVO);

}
