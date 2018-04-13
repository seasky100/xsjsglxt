package com.xsjsglxt.service.Scheduling;

import com.xsjsglxt.domain.DO.xsjsglxt_scheduling;
import com.xsjsglxt.domain.VO.Scheduling.SchedulingDTOListVO;

public interface SchedulingService {

	boolean saveScheduling(xsjsglxt_scheduling scheduling);

	xsjsglxt_scheduling getSchedulingById(xsjsglxt_scheduling scheduling);

	boolean delteShceduling(String[] scheduling_id);

	boolean updateScheduling(xsjsglxt_scheduling scheduling);

	void schedulingList(SchedulingDTOListVO schedulingListVO);

	xsjsglxt_scheduling getSchedulingByDate(xsjsglxt_scheduling scheduling);

}