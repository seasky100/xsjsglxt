package com.xsjsglxt.service.impl.Scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.xsjsglxt.dao.Scheduling.SchedulingDao;
import com.xsjsglxt.domain.DO.xsjsglxt_scheduling;
import com.xsjsglxt.domain.DO.xsjsglxt_staff;
import com.xsjsglxt.domain.DTO.Scheduling.schedulingDTO;
import com.xsjsglxt.domain.DTO.Scheduling.schedulingTimeDTO;
import com.xsjsglxt.domain.VO.Scheduling.SchedulingDTOListVO;
import com.xsjsglxt.domain.VO.Scheduling.schedulingTimeVO;
import com.xsjsglxt.service.Scheduling.SchedulingService;

import util.TeamUtil;

/**
 * 
 * @author 孙毅 排班service
 */
public class SchedulingServiceImpl implements SchedulingService {
	private SchedulingDao schedulingDao;

	public SchedulingDao getSchedulingDao() {
		return schedulingDao;
	}

	public void setSchedulingDao(SchedulingDao schedulingDao) {
		this.schedulingDao = schedulingDao;
	}

	@Override
	public boolean saveScheduling(xsjsglxt_scheduling scheduling) {
		// TODO Auto-generated method stub
		boolean flag = false;
		xsjsglxt_scheduling oldScehduling = schedulingDao.getSchedulingByDate(scheduling);
		if (oldScehduling != null && oldScehduling.getXsjsglxt_scheduling_id() != null
				&& !"".equals(oldScehduling.getXsjsglxt_scheduling_id().trim())) // 改日期已被占用
		{
			// scheduling.setXsjsglxt_scheduling_id(oldScehduling.getXsjsglxt_scheduling_id());
			// scheduling.setScheduling_gmt_create(oldScehduling.getScheduling_gmt_create());
			// scheduling.setScheduling_gmt_modified(oldScehduling.getScheduling_gmt_modified());
			// flag = schedulingDao.saveScheduling(scheduling);
			return false;
		} else {
			scheduling.setXsjsglxt_scheduling_id(TeamUtil.getUuid());
			scheduling.setScheduling_gmt_create(TeamUtil.getStringSecond());
			scheduling.setScheduling_gmt_modified(TeamUtil.getStringSecond());
			flag = schedulingDao.saveScheduling(scheduling);
			return flag;
		}

	}

	@Override
	public xsjsglxt_scheduling getSchedulingById(xsjsglxt_scheduling scheduling) {
		// TODO Auto-generated method stub
		xsjsglxt_scheduling newScheduling = schedulingDao.getSchedulingById(scheduling.getXsjsglxt_scheduling_id());
		System.out.println(newScheduling.toString());
		scheduling = newScheduling;
		return scheduling;
	}

	@Override
	public boolean delteShceduling(String[] scheduling_id) {
		// TODO Auto-generated method stub
		for (int s = 0; s < scheduling_id.length; s++) {
			boolean flag = schedulingDao.deleteScheduling(scheduling_id[s]);
		}
		return true;
	}

	@Override
	public boolean updateScheduling(xsjsglxt_scheduling scheduling) {
		// TODO Auto-generated method stub
		xsjsglxt_scheduling oldScheduling = schedulingDao.getSchedulingByDate(scheduling);
		if (oldScheduling != null && oldScheduling.getXsjsglxt_scheduling_id() != null
				&& oldScheduling.getXsjsglxt_scheduling_id().trim().length() > 0) {
			if (!oldScheduling.getXsjsglxt_scheduling_id().trim()
					.equals(scheduling.getXsjsglxt_scheduling_id().trim())) {
				// 修改了日期并且当前日期已被占用
				return false;
			} else {
				// 未修改日期
				xsjsglxt_scheduling currScheduling = schedulingDao
						.getSchedulingById(scheduling.getXsjsglxt_scheduling_id());
				scheduling.setScheduling_gmt_create(currScheduling.getScheduling_gmt_create());
				scheduling.setScheduling_gmt_modified(TeamUtil.getStringSecond());
				schedulingDao.updateScheduling(scheduling);
				return true;
			}
		} else { // 修改了日期当前日期没被排班直接修改
			xsjsglxt_scheduling currScheduling = schedulingDao
					.getSchedulingById(scheduling.getXsjsglxt_scheduling_id());
			scheduling.setScheduling_gmt_create(currScheduling.getScheduling_gmt_create());
			scheduling.setScheduling_gmt_modified(TeamUtil.getStringSecond());
			schedulingDao.updateScheduling(scheduling);
			return true;
		}

	}

	@Override
	public void schedulingList(SchedulingDTOListVO schedulingListVO) {
		// TODO Auto-generated method stub
		int count = schedulingDao.getSchedulingCount(schedulingListVO);
		schedulingListVO.setPageSize(31);
		schedulingListVO.setTotalCount(count);
		schedulingListVO.setTotalPage((int) Math.ceil((double) count / schedulingListVO.getPageSize()));
		List<schedulingDTO> dtoList = schedulingDao.getSchedulingByPage(schedulingListVO);
		schedulingListVO.setSchedulingDTOList(dtoList);
	}

	@Override
	public xsjsglxt_scheduling getSchedulingByDate(xsjsglxt_scheduling scheduling) {
		// TODO Auto-generated method stub

		xsjsglxt_scheduling r = schedulingDao.getSchedulingByDate(scheduling);
		return r;
	}

	@Override
	public void schedulingStastics(schedulingTimeVO schedulingTimeVO) {
		// TODO Auto-generated method stub
		System.out.println(schedulingTimeVO.getQueryName());
		List<xsjsglxt_staff> stasticsStaff = schedulingDao.getStaffByQuery(schedulingTimeVO.getQueryName());
		List<schedulingTimeDTO> list = new ArrayList<schedulingTimeDTO>();
		if (stasticsStaff != null && stasticsStaff.size() > 0) {
			for (xsjsglxt_staff xsjsglxt_staff : stasticsStaff) {
				schedulingTimeDTO time = new schedulingTimeDTO();
				time.setPolicemanName(xsjsglxt_staff.getXsjsglxt_name());
				int schedulingCount = schedulingDao.getSchedulingTime(schedulingTimeVO,
						xsjsglxt_staff.getXsjsglxt_name());
				time.setSchedulingTime(schedulingCount + "天");
				int outCount = schedulingDao.getOutTime(schedulingTimeVO, xsjsglxt_staff.getXsjsglxt_name());
				time.setOutTime(outCount + "天");
				int overCount = schedulingDao.getOverTime(schedulingTimeVO, xsjsglxt_staff.getXsjsglxt_name());
				time.setOverTime(overCount + "次");
				int patrolCount = schedulingDao.getPatrolTime(schedulingTimeVO, xsjsglxt_staff.getXsjsglxt_name());
				time.setPatrolTime(patrolCount + "次");
				int evectionCount = schedulingDao.getEvectionTime(schedulingTimeVO,
						xsjsglxt_staff.getXsjsglxt_staff_id());
				time.setEvectionTime(evectionCount + "次");
				list.add(time);
			}
			schedulingTimeVO.setTimeDTOList(list);
		}
	}

	@Override
	public boolean updateSchedulingLog(xsjsglxt_scheduling scheduling) {
		try {
			schedulingDao.updateScheduling(scheduling);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
