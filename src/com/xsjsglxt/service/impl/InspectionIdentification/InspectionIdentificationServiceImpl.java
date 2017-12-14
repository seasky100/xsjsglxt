package com.xsjsglxt.service.impl.InspectionIdentification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.struts2.ServletActionContext;

import com.xsjsglxt.dao.InspectionIdentification.InspectionIdentificationDao;
import com.xsjsglxt.domain.DO.xsjsglxt_appraisal_letter;
import com.xsjsglxt.domain.DO.xsjsglxt_check_entrustment_book;
import com.xsjsglxt.domain.DO.xsjsglxt_damage_inspection_record;
import com.xsjsglxt.domain.DO.xsjsglxt_death_inspection_record;
import com.xsjsglxt.domain.DO.xsjsglxt_identifieder_case_confirm_book;
import com.xsjsglxt.domain.DO.xsjsglxt_inspection_record;
import com.xsjsglxt.domain.DO.xsjsglxt_not_acceptance_entrustment_inform;
import com.xsjsglxt.domain.DTO.InspectionIdentification.EntrustmentBookManagementDTO;
import com.xsjsglxt.domain.VO.InspectionIdentification.EntrustmentBookManagementVO;
import com.xsjsglxt.service.InspectionIdentification.InspectionIdentificationService;

import util.TeamUtil;
import util.XwpfTUtil;

public class InspectionIdentificationServiceImpl implements InspectionIdentificationService {
	private InspectionIdentificationDao inspectionIdentificationDao;

	public void setInspectionIdentificationDao(InspectionIdentificationDao inspectionIdentificationDao) {
		this.inspectionIdentificationDao = inspectionIdentificationDao;
	}

	// 保存痕迹检验委托书
	@Override
	public int saveTranceCheckBook(xsjsglxt_check_entrustment_book tranceCheckBook) {
		// 状态 已申请、已受理、已记录、鉴定完成
		tranceCheckBook.setCheck_entrustment_book_state("正在申请");
		tranceCheckBook.setXsjsglxt_check_entrustment_book_id(TeamUtil.getUuid());
		// 查找最大值,并且填写编号
		int i = inspectionIdentificationDao.getMaxCheckNum(TeamUtil.getCurrentYear(),
				tranceCheckBook.getCheck_entrustment_book_type()) + 1;
		tranceCheckBook.setCheck_entrustment_book_num(TeamUtil.getCurrentYear() + String.format("%04d", i));
		tranceCheckBook.setCheck_entrustment_book_gmt_create(TeamUtil.getStringSecond());
		tranceCheckBook.setCheck_entrustment_book_gmt_modified(tranceCheckBook.getCheck_entrustment_book_gmt_create());
		return inspectionIdentificationDao.saveObject(tranceCheckBook);
	}

	// 分页获取委托书
	@Override
	public EntrustmentBookManagementVO getListCheckEntrustmentBook(EntrustmentBookManagementVO checkEntrustmentBookVO) {
		EntrustmentBookManagementDTO entrustmentBookManagementDTO = new EntrustmentBookManagementDTO();
		List<EntrustmentBookManagementDTO> listEntrustmentBookManagementDTO = new ArrayList<EntrustmentBookManagementDTO>();
		xsjsglxt_appraisal_letter appraisalLetter = new xsjsglxt_appraisal_letter();
		xsjsglxt_identifieder_case_confirm_book identifiederCaseConfirmBook = new xsjsglxt_identifieder_case_confirm_book();
		xsjsglxt_inspection_record inspectionRecord = new xsjsglxt_inspection_record();
		xsjsglxt_death_inspection_record death_inspection_record = null;
		xsjsglxt_damage_inspection_record damage_inspection_record = null;
		xsjsglxt_not_acceptance_entrustment_inform notAcceptanceEntrustmentInform = new xsjsglxt_not_acceptance_entrustment_inform();
		List<xsjsglxt_check_entrustment_book> listCheckEntrustmentBook = new ArrayList<xsjsglxt_check_entrustment_book>();
		// 根据筛选获取总计路数
		int i = inspectionIdentificationDao.getCountByPageAndSearch(checkEntrustmentBookVO);
		checkEntrustmentBookVO.setTotalRecords(i);
		checkEntrustmentBookVO.setTotalPages(((i - 1) / checkEntrustmentBookVO.getPageSize()) + 1);
		if (checkEntrustmentBookVO.getPageIndex() <= 1) {
			checkEntrustmentBookVO.setHavePrePage(false);
		} else {
			checkEntrustmentBookVO.setHavePrePage(true);
		}
		if (checkEntrustmentBookVO.getPageIndex() >= checkEntrustmentBookVO.getTotalPages()) {
			checkEntrustmentBookVO.setHaveNextPage(false);
		} else {
			checkEntrustmentBookVO.setHaveNextPage(true);
		}
		// 获取分页所有
		listCheckEntrustmentBook = inspectionIdentificationDao
				.getListCheckEntrustmentBookByPage(checkEntrustmentBookVO);
		for (xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book : listCheckEntrustmentBook) {
			appraisalLetter = new xsjsglxt_appraisal_letter();
			inspectionRecord = new xsjsglxt_inspection_record();
			entrustmentBookManagementDTO = new EntrustmentBookManagementDTO();
			identifiederCaseConfirmBook = new xsjsglxt_identifieder_case_confirm_book();
			death_inspection_record = new xsjsglxt_death_inspection_record();
			damage_inspection_record = new xsjsglxt_damage_inspection_record();
			notAcceptanceEntrustmentInform = new xsjsglxt_not_acceptance_entrustment_inform();
			entrustmentBookManagementDTO.setXsjsglxt_check_entrustment_book(xsjsglxt_check_entrustment_book);
			// 根据Id获取鉴定事项确认书表
			identifiederCaseConfirmBook = inspectionIdentificationDao.getIdentifiederCaseConfirmBookById(
					xsjsglxt_check_entrustment_book.getXsjsglxt_check_entrustment_book_id());
			if (identifiederCaseConfirmBook != null) {
				entrustmentBookManagementDTO.setXsjsglxt_identifieder_case_confirm_book(identifiederCaseConfirmBook);
			}
			// 根据Id获取不受理委托鉴定告知表
			notAcceptanceEntrustmentInform = inspectionIdentificationDao.getNotAcceptanceEntrustmentInform(
					xsjsglxt_check_entrustment_book.getXsjsglxt_check_entrustment_book_id());
			if (notAcceptanceEntrustmentInform != null) {
				entrustmentBookManagementDTO
						.setXsjsglxt_not_acceptance_entrustment_inform(notAcceptanceEntrustmentInform);
			}
			// 根据委托ID获取检验记录表
			inspectionRecord = inspectionIdentificationDao.getInspectionRecordById(xsjsglxt_check_entrustment_book.getXsjsglxt_check_entrustment_book_id());
			if (inspectionRecord != null) {
				entrustmentBookManagementDTO.setXsjsglxt_inspection_record(inspectionRecord);
			}
			// 根据委托书Id获取尸体检验记录表
			death_inspection_record = inspectionIdentificationDao.getDeathInspectionRecordById(xsjsglxt_check_entrustment_book.getXsjsglxt_check_entrustment_book_id());
			if (death_inspection_record != null) {
				entrustmentBookManagementDTO.setXsjsglxt_death_inspection_record(death_inspection_record);
			}
			// 损伤检验记录表
			damage_inspection_record = inspectionIdentificationDao.getDamageInspectionRecordById(xsjsglxt_check_entrustment_book.getXsjsglxt_check_entrustment_book_id());
			if (damage_inspection_record != null) {
				entrustmentBookManagementDTO.setXsjsglxt_damage_inspection_record(damage_inspection_record);
			}
			listEntrustmentBookManagementDTO.add(entrustmentBookManagementDTO);
			// 根据委托Id获取鉴定文书
			appraisalLetter = inspectionIdentificationDao.getAppraisalLetterById(xsjsglxt_check_entrustment_book.getXsjsglxt_check_entrustment_book_id());
			if (appraisalLetter != null) {
				entrustmentBookManagementDTO.setXsjsglxt_appraisal_letter(appraisalLetter);
			}
		}
		checkEntrustmentBookVO.setListEntrustmentBookManagementDTO(listEntrustmentBookManagementDTO);
		return checkEntrustmentBookVO;
	}

	// 批量删除委托书ID
	@Override
	public int deleteListCheckEntrustmentBook(List<String> listCheckEntrustmentBookId) {
		int i = 0;
		xsjsglxt_not_acceptance_entrustment_inform xsjsglxt_not_acceptance_entrustment_inform = null;
		xsjsglxt_inspection_record xsjsglxt_inspection_record = null;
		xsjsglxt_appraisal_letter xsjsglxt_appraisal_letter = null;
		xsjsglxt_identifieder_case_confirm_book xsjsglxt_identifieder_case_confirm_book = null;
		xsjsglxt_death_inspection_record xsjsglxt_death_inspection_record = null;
		xsjsglxt_damage_inspection_record xsjsglxt_damage_inspection_record = null;
		for (String checkEntrustmentBookId : listCheckEntrustmentBookId) {
			xsjsglxt_death_inspection_record = new xsjsglxt_death_inspection_record();
			xsjsglxt_identifieder_case_confirm_book = new xsjsglxt_identifieder_case_confirm_book();
			xsjsglxt_damage_inspection_record = new xsjsglxt_damage_inspection_record();
			xsjsglxt_inspection_record = new xsjsglxt_inspection_record();
			xsjsglxt_appraisal_letter = new xsjsglxt_appraisal_letter();
			xsjsglxt_not_acceptance_entrustment_inform = new xsjsglxt_not_acceptance_entrustment_inform();
			// 根据ID删除确认书
			xsjsglxt_identifieder_case_confirm_book = inspectionIdentificationDao.getIdentifiederCaseConfirmBookById(checkEntrustmentBookId);
			if (xsjsglxt_identifieder_case_confirm_book != null) {
				i = inspectionIdentificationDao.deleteIdentifiederCaseConfirmBookId(xsjsglxt_identifieder_case_confirm_book.getXsjsglxt_identifieder_case_confirm_book_id());
				if (i == 2)
					return -1;
			}
			// 根据ID删除不受理书
			xsjsglxt_not_acceptance_entrustment_inform = inspectionIdentificationDao.getNotAcceptanceEntrustmentInform(checkEntrustmentBookId);
			if (xsjsglxt_not_acceptance_entrustment_inform != null) {
				i = inspectionIdentificationDao
						.deleteNotAcceptanceEntrusmentInformById(xsjsglxt_not_acceptance_entrustment_inform.getXsjsglxt_not_acceptance_entrustment_inform_id());
				if (i == 2)
					return -1;
			}
			// 根据ID删除记录表
			xsjsglxt_inspection_record = inspectionIdentificationDao.getInspectionRecordById(checkEntrustmentBookId);
			if (xsjsglxt_inspection_record != null) {
				i = inspectionIdentificationDao.deleteInsoectionRecordById(xsjsglxt_inspection_record.getXsjsglxt_inspection_record_id());
				if (i == 2)
					return -1;
			}
			// 根据Id删除尸体检验记录表
			xsjsglxt_death_inspection_record = inspectionIdentificationDao.getDeathInspectionRecordById(checkEntrustmentBookId);
			if (xsjsglxt_death_inspection_record != null) {
				i = inspectionIdentificationDao.deleteDeathInspectionRecordById(xsjsglxt_death_inspection_record.getXsjsglxt_death_inspection_record_id());
				if (i == 2)
					return -1;
			}
			// 根据Id删除损伤检验记录表
			xsjsglxt_damage_inspection_record = inspectionIdentificationDao.getDamageInspectionRecordById(checkEntrustmentBookId);
			if (xsjsglxt_damage_inspection_record != null) {
				i = inspectionIdentificationDao.deleteDamageInspectionRecordById(xsjsglxt_damage_inspection_record.getXsjsglxt_damage_inspection_record_id());
				if (i == 2)
					return -1;
			}

			// 删除鉴定文书
			xsjsglxt_appraisal_letter = inspectionIdentificationDao.getAppraisalLetterById(checkEntrustmentBookId);
			if (xsjsglxt_inspection_record != null) {
				i = inspectionIdentificationDao.deleteAppraisalLetter(xsjsglxt_appraisal_letter.getXsjsglxt_appraisal_letter_id());
				if (i == 2)
					return -1;
			}
			// 删除委托书
			i = inspectionIdentificationDao.deleteCheckEntrustmentBookById(checkEntrustmentBookId);
			if (i == 2) {
				return -1;
			}
		}
		return i;
	}

	// 填写鉴定事项确认书
	@Override
	public int saveIdentifiederCaseConfirmBook(xsjsglxt_identifieder_case_confirm_book identifiederCaseConfirmBook) {
		int i = 2;
		// 处于正在申请
		if (!("正在申请".equals(entrustmentBookState(identifiederCaseConfirmBook.getIdentifieder_case_confirm_book_belong_entrustment_book().trim())))) {
			return 3;
		}
		// 填写鉴定事项确认书
		identifiederCaseConfirmBook.setXsjsglxt_identifieder_case_confirm_book_id(TeamUtil.getUuid());
		// 判断编号是否存在
		if (identifiederCaseConfirmBook.getIdentifieder_case_confirm_book_ishave_num() == 2) {
			identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_acceptance_num("");
		} else {
			// 这里需要生成确认书编号
			String type = entrustmentBookType(identifiederCaseConfirmBook.getIdentifieder_case_confirm_book_belong_entrustment_book().trim());
			if (type != null && type.trim().length() > 0) {
				int j = inspectionIdentificationDao.getMaxCofirmBook(TeamUtil.getCurrentYear(), type) + 1;
				identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_acceptance_num(TeamUtil.getCurrentYear() + String.format("%04d", j));
			} else {
				identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_acceptance_num("");
			}
		}
		identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_gmt_create(TeamUtil.getStringSecond());
		identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_gmt_modified(identifiederCaseConfirmBook.getIdentifieder_case_confirm_book_gmt_create());
		i = inspectionIdentificationDao.saveObject(identifiederCaseConfirmBook);
		if (i == 2)
			return i;
		// 更改委托书状态
		// 1.判断委托书Id是否为空
		if (identifiederCaseConfirmBook.getIdentifieder_case_confirm_book_belong_entrustment_book() != null
				&& identifiederCaseConfirmBook.getIdentifieder_case_confirm_book_belong_entrustment_book().trim()
						.length() > 0) {
			i = updateEntrustmentBookState(
					identifiederCaseConfirmBook.getIdentifieder_case_confirm_book_belong_entrustment_book().trim(),
					"申请已受理");
		}
		return i;
	}

	// 填写不受理书
	@Override
	public int saveNotAcceptanceInform(xsjsglxt_not_acceptance_entrustment_inform notAcceptanceEntrustmentInform) {
		int i = 2;
		// 处于正在申请
		if (!("正在申请".equals(entrustmentBookState(notAcceptanceEntrustmentInform.getNot_acceptance_entrustment_inform_belong_entrustment_book().trim())))) {
			return 3;
		}
		notAcceptanceEntrustmentInform.setXsjsglxt_not_acceptance_entrustment_inform_id(TeamUtil.getUuid());
		// notAcceptanceEntrustmentInform.setNot_acceptance_entrustment_inform_num("待定");
		notAcceptanceEntrustmentInform.setNot_acceptance_entrustment_inform_gmt_create(TeamUtil.getStringSecond());
		notAcceptanceEntrustmentInform.setNot_acceptance_entrustment_inform_gmt_modified(notAcceptanceEntrustmentInform.getNot_acceptance_entrustment_inform_gmt_create());
		i = inspectionIdentificationDao.saveObject(notAcceptanceEntrustmentInform);
		if (i == 2)
			return i;
		// 更改委托书状态
		// 1.判断委托书Id是否为空
		if (notAcceptanceEntrustmentInform.getNot_acceptance_entrustment_inform_belong_entrustment_book() != null
				&& notAcceptanceEntrustmentInform.getNot_acceptance_entrustment_inform_belong_entrustment_book().trim()
						.length() > 0) {
			i = updateEntrustmentBookState(notAcceptanceEntrustmentInform
					.getNot_acceptance_entrustment_inform_belong_entrustment_book().trim(), "申请被拒绝");
		}
		return i;
	}

	/**
	 * 保存损伤检验记录表
	 * 
	 * @throws IOException
	 */
	@Override
	public int saveDamageInspectionRecord(xsjsglxt_damage_inspection_record damageInspectionRecord, File[] file, String[] fileName, String[] positionFile) throws IOException {
		int i = 2;
		int x = -1;
		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
			// lj=F:\\
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}
		String path = lj + "xsjsglxt/damage/";
		// 将文件以及文件名格式化
		file = file(file, positionFile, "damage");
		fileName = fileName(fileName, positionFile, "damage");
		if (!("申请已受理".equals(entrustmentBookState(damageInspectionRecord.getDamage_inspection_record_belong_entrustment_book().trim())))) {
			return 3;
		}
		//
		damageInspectionRecord.setXsjsglxt_damage_inspection_record_id(TeamUtil.getUuid());
		// 上传图片
		for (int k = 0; k < file.length; k++) {
			path = lj + "xsjsglxt/damage/";
			if (file[k] != null) {
				path = path + damageInspectionRecord.getXsjsglxt_damage_inspection_record_id() + "_" + fileName[k];
				File newFile = new File(path);
				FileUtils.copyFile(file[k], newFile);
			}
		}
		// 设置其他字段名
		for (int j = 0; j < file.length; j++) {
			x = -1;
			if (file[j] != null) {
				x = j;
			}
			switch (x) {
			case 0:
				damageInspectionRecord.setDamage_inspection_record_picture1(fileName[x]);
				break;
			case 1:
				damageInspectionRecord.setDamage_inspection_record_picture2(fileName[x]);
				break;
			case 2:
				damageInspectionRecord.setDamage_inspection_record_picture3(fileName[x]);
				break;
			default:
				break;
			}
		}
		// 填写损伤检验记录表
		damageInspectionRecord.setDamage_inspection_record_gmt_create(TeamUtil.getStringSecond());
		damageInspectionRecord.setDamage_inspection_record_gmt_modified(damageInspectionRecord.getDamage_inspection_record_gmt_create());
		i = inspectionIdentificationDao.saveObject(damageInspectionRecord);
		if (i == 2)
			return i;
		// 更改委托书状态
		i = updateEntrustmentBookState(damageInspectionRecord.getDamage_inspection_record_belong_entrustment_book().trim(), "已记录");
		return i;
	}

	/**
	 *
	 * 保存尸体检验记录表
	 * 
	 * @throws IOException
	 *
	 */
	@Override
	public int saveDeathInspectionRecord(xsjsglxt_death_inspection_record deathInspectionRecord, File[] file, String[] fileName, String[] positionFile) throws IOException {
		int i = 2;
		int x = -1;

		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
			// lj=F:\\
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}

		String path = lj + "xsjsglxt/death/";

		// 将文件以及文件名格式化
		file = file(file, positionFile, "death");
		fileName = fileName(fileName, positionFile, "death");
		/**
		 * 这里是否需要我来进行判断
		 */
		if (!("申请已受理".equals(entrustmentBookState(deathInspectionRecord.getDeath_inspection_record_belong_entrustment_book().trim())))) {
			return 3;
		}
		deathInspectionRecord.setXsjsglxt_death_inspection_record_id(TeamUtil.getUuid());
		// 上传图片
		for (int k = 0; k < file.length; k++) {
			path = lj + "xsjsglxt/death/";
			if (file[k] != null) {
				path = path + deathInspectionRecord.getXsjsglxt_death_inspection_record_id() + "_" + fileName[k];
				File newFile = new File(path);
				FileUtils.copyFile(file[k], newFile);
			}
		}
		// 设置其他字段名
		for (int j = 0; j < file.length; j++) {
			x = -1;
			if (file[j] != null) {
				x = j;
			}
			switch (x) {
			case 0:
				deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture1(fileName[x]);
				break;
			case 1:
				deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture2(fileName[x]);
				break;
			case 2:
				deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture3(fileName[x]);
				break;
			case 3:
				deathInspectionRecord.setDeath_inspection_record_anatomy_picture1(fileName[x]);
				break;
			case 4:
				deathInspectionRecord.setDeath_inspection_record_anatomy_picture2(fileName[x]);
				break;
			case 5:
				deathInspectionRecord.setDeath_inspection_record_anatomy_picture3(fileName[x]);
				break;
			default:
				break;
			}
		}
		// 填写尸体检验记录表 deathInspectionRecord
		deathInspectionRecord.setDeath_inspection_record_gmt_create(TeamUtil.getStringSecond());
		deathInspectionRecord.setDeath_inspection_record_gmt_modified(deathInspectionRecord.getDeath_inspection_record_gmt_create());
		i = inspectionIdentificationDao.saveObject(deathInspectionRecord);
		if (i == 2)
			return i;
		// 更改委托书状态
		i = updateEntrustmentBookState(deathInspectionRecord.getDeath_inspection_record_belong_entrustment_book().trim(), "已记录");
		return i;
	}

	// 填写痕迹检验记录表
	/**
	 * 1 OK 2 失败 3 未达到执行条件
	 */
	@Override
	public int saveInspectionRecord(xsjsglxt_inspection_record inspectionRecord) {
		int i = 2;
		// 1.查询委托书表
		/**
		 * 这里是否需要我来进行判断
		 */
		if (!("申请已受理".equals(entrustmentBookState(inspectionRecord.getInspection_belong_entrustment_book().trim())))) {
			return 3;
		}
		/**
		 * 
		 */
		// 填写检验记录表
		inspectionRecord.setXsjsglxt_inspection_record_id(TeamUtil.getUuid());
		inspectionRecord.setInspection_gmt_create(TeamUtil.getStringSecond());
		inspectionRecord.setInspection_gmt_modified(inspectionRecord.getInspection_gmt_create());
		i = inspectionIdentificationDao.saveObject(inspectionRecord);
		if (i == 2)
			return i;
		// 更改委托书状态
		i = updateEntrustmentBookState(inspectionRecord.getInspection_belong_entrustment_book().trim(), "已记录");
		return i;
	}

	// 填写鉴定文书表
	@Override
	public int saveAppraisalLetter(xsjsglxt_appraisal_letter appraisalLetter) {
		int i = 2;
		//
		if (!("已记录".equals(entrustmentBookState(appraisalLetter.getAppraisal_letter_belong_entrustment_book().trim())))) {
			return 3;
		}
		// 1.查询委托书表的鉴定要求给予鉴定书类型
		switch (entrustmentBookRequest(appraisalLetter.getAppraisal_letter_belong_entrustment_book().trim())) {
		case "损伤鉴定":
			appraisalLetter.setAppraisal_letter_type("损伤");
			break;
		case "死因鉴定":
			appraisalLetter.setAppraisal_letter_type("尸体");
			break;
		default:
			appraisalLetter.setAppraisal_letter_type("痕迹");
			break;
		}
		// 给予编号
		int k = inspectionIdentificationDao.getMaxLetterNum(TeamUtil.getCurrentYear(), appraisalLetter.getAppraisal_letter_type()) + 1;
		appraisalLetter.setAppraisal_letter_num(TeamUtil.getCurrentYear() + String.format("%04d", k));
		appraisalLetter.setXsjsglxt_appraisal_letter_id(TeamUtil.getUuid());
		appraisalLetter.setAppraisal_letter_gmt_create(TeamUtil.getStringSecond());
		appraisalLetter.setAppraisal_letter_gmt_modified(appraisalLetter.getAppraisal_letter_gmt_create());
		i = inspectionIdentificationDao.saveObject(appraisalLetter);
		if (i == 2)
			return i;
		// 更改委托书状态
		i = updateEntrustmentBookState(appraisalLetter.getAppraisal_letter_belong_entrustment_book().trim(), "已鉴定");
		return i;
	}

	// 更改委托书
	// 3 传过来的参数有误
	@Override
	public int updateTranceCheckBook(xsjsglxt_check_entrustment_book tranceCheckBook) {
		xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book = new xsjsglxt_check_entrustment_book();
		// 获取原有对象
		xsjsglxt_check_entrustment_book = inspectionIdentificationDao.getCheckEntrustmentBookById(tranceCheckBook.getXsjsglxt_check_entrustment_book_id());
		if (xsjsglxt_check_entrustment_book == null)
			return 3;
		tranceCheckBook.setCheck_entrustment_book_physical_evidence(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_physical_evidence());
		tranceCheckBook.setCheck_entrustment_book_num(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num());
		tranceCheckBook.setCheck_entrustment_book_gmt_modified(TeamUtil.getStringSecond());
		tranceCheckBook.setCheck_entrustment_book_type(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_type());
		tranceCheckBook.setCheck_entrustment_book_state(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_state());
		tranceCheckBook.setCheck_entrustment_book_gmt_create(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_gmt_create());
		return inspectionIdentificationDao.saveObject(tranceCheckBook);
	}

	// 更改鉴定事项确认书表
	@Override
	public int updateIdentifiederCaseConfirmBook(xsjsglxt_identifieder_case_confirm_book identifiederCaseConfirmBook) {
		xsjsglxt_identifieder_case_confirm_book xsjsglxt_identifieder_case_confirm_book = new xsjsglxt_identifieder_case_confirm_book();
		xsjsglxt_identifieder_case_confirm_book = inspectionIdentificationDao
				.getIdentifiederCaseConfirmBookByOwnId(identifiederCaseConfirmBook.getXsjsglxt_identifieder_case_confirm_book_id());
		if (xsjsglxt_identifieder_case_confirm_book == null)
			return 3;
		identifiederCaseConfirmBook
				.setIdentifieder_case_confirm_book_belong_entrustment_book(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_belong_entrustment_book());
		// identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_acceptance_num(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_num());
		identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_gmt_create(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_gmt_create());
		identifiederCaseConfirmBook.setIdentifieder_case_confirm_book_gmt_modified(TeamUtil.getStringSecond());
		return inspectionIdentificationDao.saveObject(identifiederCaseConfirmBook);
	}

	// 不受理委托鉴定告知表
	@Override
	public int updateNotAcceptanceInform(xsjsglxt_not_acceptance_entrustment_inform notAcceptanceEntrustmentInform) {
		xsjsglxt_not_acceptance_entrustment_inform xsjsglxt_not_acceptance_entrustment_inform = new xsjsglxt_not_acceptance_entrustment_inform();
		xsjsglxt_not_acceptance_entrustment_inform = inspectionIdentificationDao
				.getNotAcceptanceEntrustmentInformByOwnId(notAcceptanceEntrustmentInform.getXsjsglxt_not_acceptance_entrustment_inform_id());
		if (xsjsglxt_not_acceptance_entrustment_inform == null)
			return 3;
		notAcceptanceEntrustmentInform.setNot_acceptance_entrustment_inform_belong_entrustment_book(
				xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_belong_entrustment_book());
		notAcceptanceEntrustmentInform
				.setNot_acceptance_entrustment_inform_gmt_create(xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_gmt_create());
		notAcceptanceEntrustmentInform.setNot_acceptance_entrustment_inform_gmt_modified(TeamUtil.getStringSecond());
		return inspectionIdentificationDao.saveObject(notAcceptanceEntrustmentInform);
	}

	// 更改尸体检验记录表
	@Override
	public int updateDeathInspectionRecord(xsjsglxt_death_inspection_record deathInspectionRecord, File[] updateDeathFile, String[] updateDeathFileName, String[] positionFile)
			throws IOException {
		// 将文件以及文件名格式化
		updateDeathFile = file(updateDeathFile, positionFile, "death");
		updateDeathFileName = fileName(updateDeathFileName, positionFile, "death");
		xsjsglxt_death_inspection_record death_inspection_record = new xsjsglxt_death_inspection_record();
		death_inspection_record = inspectionIdentificationDao.getDeathInspectionRecordOwnId(deathInspectionRecord.getXsjsglxt_death_inspection_record_id());
		if (death_inspection_record == null)
			return 3;
		deathInspectionRecord.setDeath_inspection_record_belong_entrustment_book(death_inspection_record.getDeath_inspection_record_belong_entrustment_book());
		// 更改图片记录
		for (int k = 0; k < updateDeathFile.length; k++) {
			if (updateDeathFile[k] != null) {
				switch (k) {
				case 0:
					death_inspection_record = uploadDeath("尸表检验图1", updateDeathFile[k], death_inspection_record.getDeath_inspection_record_autopsy_table_test_picture1(),
							death_inspection_record,
							updateDeathFileName[k]);
					break;
				case 1:
					death_inspection_record = uploadDeath("尸表检验图2", updateDeathFile[k], death_inspection_record.getDeath_inspection_record_autopsy_table_test_picture2(),
							death_inspection_record,
							updateDeathFileName[k]);
					break;
				case 2:
					death_inspection_record = uploadDeath("尸表检验图3", updateDeathFile[k], death_inspection_record.getDeath_inspection_record_autopsy_table_test_picture3(),
							death_inspection_record,
							updateDeathFileName[k]);
					break;
				case 3:
					death_inspection_record = uploadDeath("解剖检验图1", updateDeathFile[k], death_inspection_record.getDeath_inspection_record_anatomy_picture1(),
							death_inspection_record,
							updateDeathFileName[k]);
					break;
				case 4:
					death_inspection_record = uploadDeath("解剖检验图2", updateDeathFile[k], death_inspection_record.getDeath_inspection_record_anatomy_picture2(),
							death_inspection_record,
							updateDeathFileName[k]);
					break;
				case 5:
					death_inspection_record = uploadDeath("解剖检验图3", updateDeathFile[k], death_inspection_record.getDeath_inspection_record_anatomy_picture3(),
							death_inspection_record,
							updateDeathFileName[k]);
					break;
				default:
					break;
				}
			}
		}
		deathInspectionRecord.setDeath_inspection_record_anatomy_picture1(
				death_inspection_record.getDeath_inspection_record_anatomy_picture1());
		deathInspectionRecord.setDeath_inspection_record_anatomy_picture2(
				death_inspection_record.getDeath_inspection_record_anatomy_picture2());
		deathInspectionRecord.setDeath_inspection_record_anatomy_picture3(
				death_inspection_record.getDeath_inspection_record_anatomy_picture3());
		deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture1(
				death_inspection_record.getDeath_inspection_record_autopsy_table_test_picture1());
		deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture2(
				death_inspection_record.getDeath_inspection_record_autopsy_table_test_picture2());
		deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture3(
				death_inspection_record.getDeath_inspection_record_autopsy_table_test_picture3());
		deathInspectionRecord.setDeath_inspection_record_gmt_create(death_inspection_record.getDeath_inspection_record_gmt_create());
		deathInspectionRecord.setDeath_inspection_record_gmt_modified(TeamUtil.getStringSecond());
		return inspectionIdentificationDao.saveObject(deathInspectionRecord);
	}

	// 更改损伤检验记录表
	@Override
	public int updateDamageInspectionRecord(xsjsglxt_damage_inspection_record damageInspectionRecord, File[] updateDamageFile, String[] updateDamageFileName,
			String[] positionFile) throws IOException {
		// 将文件以及文件名格式化
		updateDamageFile = file(updateDamageFile, positionFile, "death");
		updateDamageFileName = fileName(updateDamageFileName, positionFile, "death");
		xsjsglxt_damage_inspection_record xsjsglxt_damage_inspection_record = new xsjsglxt_damage_inspection_record();
		xsjsglxt_damage_inspection_record = inspectionIdentificationDao.getDamageInspectionRecordByOwnId(damageInspectionRecord.getXsjsglxt_damage_inspection_record_id());
		if (xsjsglxt_damage_inspection_record == null)
			return 3;
		damageInspectionRecord.setDamage_inspection_record_belong_entrustment_book(xsjsglxt_damage_inspection_record.getDamage_inspection_record_belong_entrustment_book());
		// 更改图片记录

		for (int k = 0; k < updateDamageFile.length; k++) {
			if (updateDamageFile[k] != null) {
				switch (k) {
				case 0:
					xsjsglxt_damage_inspection_record = uploadDamage("损伤检验图1", updateDamageFile[k], xsjsglxt_damage_inspection_record.getDamage_inspection_record_picture1(),
							xsjsglxt_damage_inspection_record, updateDamageFileName[k]);
					break;
				case 1:
					xsjsglxt_damage_inspection_record = uploadDamage("损伤检验图2", updateDamageFile[k], xsjsglxt_damage_inspection_record.getDamage_inspection_record_picture2(),
							xsjsglxt_damage_inspection_record, updateDamageFileName[k]);
					break;
				case 2:
					xsjsglxt_damage_inspection_record = uploadDamage("损伤检验图3", updateDamageFile[k], xsjsglxt_damage_inspection_record.getDamage_inspection_record_picture3(),
							xsjsglxt_damage_inspection_record, updateDamageFileName[k]);
					break;
				default:
					break;
				}
			}
		}
		damageInspectionRecord.setDamage_inspection_record_picture1(xsjsglxt_damage_inspection_record.getDamage_inspection_record_picture1());
		damageInspectionRecord.setDamage_inspection_record_picture2(xsjsglxt_damage_inspection_record.getDamage_inspection_record_picture2());
		damageInspectionRecord.setDamage_inspection_record_picture3(xsjsglxt_damage_inspection_record.getDamage_inspection_record_picture3());
		damageInspectionRecord.setDamage_inspection_record_gmt_create(xsjsglxt_damage_inspection_record.getDamage_inspection_record_gmt_create());
		damageInspectionRecord.setDamage_inspection_record_gmt_modified(TeamUtil.getStringSecond());
		return inspectionIdentificationDao.saveObject(damageInspectionRecord);
	}

	// 检验记录表
	@Override
	public int updateInspectionRecord(xsjsglxt_inspection_record inspectionRecord) {
		xsjsglxt_inspection_record xsjsglxt_inspection_record = new xsjsglxt_inspection_record();
		xsjsglxt_inspection_record = inspectionIdentificationDao.getInspectionRecordByOwnId(inspectionRecord.getXsjsglxt_inspection_record_id());
		if (xsjsglxt_inspection_record == null)
			return 3;
		inspectionRecord.setInspection_belong_entrustment_book(xsjsglxt_inspection_record.getInspection_belong_entrustment_book());
		inspectionRecord.setInspection_gmt_create(xsjsglxt_inspection_record.getInspection_gmt_create());
		inspectionRecord.setInspection_gmt_modified(TeamUtil.getStringSecond());
		return inspectionIdentificationDao.saveObject(inspectionRecord);
	}

	// 更改鉴定文书
	@Override
	public int updateAppraisalLetter(xsjsglxt_appraisal_letter appraisalLetter) {
		xsjsglxt_appraisal_letter xsjsglxt_appraisal_letter = new xsjsglxt_appraisal_letter();
		xsjsglxt_appraisal_letter = inspectionIdentificationDao.getAppraisalLetterByOwnId(appraisalLetter.getXsjsglxt_appraisal_letter_id());
		if (xsjsglxt_appraisal_letter == null)
			return 3;
		appraisalLetter.setAppraisal_letter_belong_entrustment_book(xsjsglxt_appraisal_letter.getAppraisal_letter_belong_entrustment_book());
		appraisalLetter.setAppraisal_letter_num(xsjsglxt_appraisal_letter.getAppraisal_letter_num());
		appraisalLetter.setAppraisal_letter_type(xsjsglxt_appraisal_letter.getAppraisal_letter_type());
		appraisalLetter.setAppraisal_letter_gmt_create(xsjsglxt_appraisal_letter.getAppraisal_letter_gmt_create());
		appraisalLetter.setAppraisal_letter_gmt_modified(TeamUtil.getStringSecond());
		return inspectionIdentificationDao.saveObject(appraisalLetter);
	}

	// 获取导出确认书的编号
	@Override
	public String exportIdentifiederCaseConfirmBookName(String id) {

		xsjsglxt_identifieder_case_confirm_book xsjsglxt_identifieder_case_confirm_book = new xsjsglxt_identifieder_case_confirm_book();
		xsjsglxt_identifieder_case_confirm_book = inspectionIdentificationDao.getIdentifiederCaseConfirmBookByOwnId(id);
		if (xsjsglxt_identifieder_case_confirm_book != null && xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_num() != null) {
			return xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_num();
		}
		return null;
	}

	// 获取导出的委托书的编号
	@Override
	public String exportTraceCheckBookName(String id) {
		xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book = new xsjsglxt_check_entrustment_book();
		xsjsglxt_check_entrustment_book = inspectionIdentificationDao.getCheckEntrustmentBookById(id);
		if (xsjsglxt_check_entrustment_book != null) {
			return xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num();
		}
		return null;
	}

	// 导出委托书
	@Override
	public File exportTranceCheckBook(String id) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}
		params.putAll(mapTranceCheckBook(id));
		XwpfTUtil xwpfTUtil = new XwpfTUtil();
		XWPFDocument doc;
		String fileNameInResource = ServletActionContext.getServletContext().getRealPath("/DocTem/xsjsglxt_entrustment_book.docx");
		InputStream is;
		is = new FileInputStream(fileNameInResource);
		doc = new XWPFDocument(is);
		xwpfTUtil.replaceInPara(doc, params);
		xwpfTUtil.replaceInTable(doc, params);
		OutputStream os = new FileOutputStream(lj + "kokokoko.docx");
		doc.write(os);
		xwpfTUtil.close(os);
		xwpfTUtil.close(is);
		os.flush();
		os.close();
		return new File(lj + "kokokoko.docx");
	}

	// 导出确认书
	@Override
	public File exportIdentifiederCaseConfirmBook(String id) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}
		params.putAll(mapIdentifiederCaseConfirmBook(id));
		XwpfTUtil xwpfTUtil = new XwpfTUtil();
		XWPFDocument doc;
		String fileNameInResource = ServletActionContext.getServletContext().getRealPath("/DocTem/xsjsglxt_confirm_book.docx");
		InputStream is;
		is = new FileInputStream(fileNameInResource);
		doc = new XWPFDocument(is);
		xwpfTUtil.replaceInPara(doc, params);
		xwpfTUtil.replaceInTable(doc, params);
		OutputStream os = new FileOutputStream(lj + "kokokoko.docx");
		doc.write(os);
		xwpfTUtil.close(os);
		xwpfTUtil.close(is);
		os.flush();
		os.close();
		return new File(lj + "kokokoko.docx");
	}

	// 导出受理回执表
	@Override
	public File exportAcceptanceReturnReceipt(String id) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}
		params.putAll(mapAcceptanceIdentifieder(id));
		XwpfTUtil xwpfTUtil = new XwpfTUtil();
		XWPFDocument doc;
		String fileNameInResource = ServletActionContext.getServletContext().getRealPath("/DocTem/xsjsglxt_entrustment_book.docx");
		InputStream is;
		is = new FileInputStream(fileNameInResource);
		doc = new XWPFDocument(is);
		xwpfTUtil.replaceInPara(doc, params);
		xwpfTUtil.replaceInTable(doc, params);
		OutputStream os = new FileOutputStream(lj + "kokokoko.docx");
		doc.write(os);
		xwpfTUtil.close(os);
		xwpfTUtil.close(is);
		os.flush();
		os.close();
		return new File(lj + "kokokoko.docx");

	}

	// 导出不受理告知
	public File exportNotAcceptanceIdentifieder(String id) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}
		params.putAll(mapNotAcceptanceIdentifieder(id));
		XwpfTUtil xwpfTUtil = new XwpfTUtil();
		XWPFDocument doc;
		String fileNameInResource = ServletActionContext.getServletContext().getRealPath("/DocTem/xsjsglxt_entrustment_book.docx");
		InputStream is;
		is = new FileInputStream(fileNameInResource);
		doc = new XWPFDocument(is);
		xwpfTUtil.replaceInPara(doc, params);
		xwpfTUtil.replaceInTable(doc, params);
		OutputStream os = new FileOutputStream(lj + "kokokoko.docx");
		doc.write(os);
		xwpfTUtil.close(os);
		xwpfTUtil.close(is);
		os.flush();
		os.close();
		return new File(lj + "kokokoko.docx");
	}

	// 导出不受理回执的编号
	@Override
	public String exportNotAccetpBookName(String id) {
		xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book = new xsjsglxt_check_entrustment_book();
		xsjsglxt_not_acceptance_entrustment_inform xsjsglxt_not_acceptance_entrustment_inform = new xsjsglxt_not_acceptance_entrustment_inform();
		xsjsglxt_not_acceptance_entrustment_inform = inspectionIdentificationDao.getNotAcceptanceEntrustmentInformByOwnId(id);
		if (xsjsglxt_not_acceptance_entrustment_inform != null) {
			if (xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_belong_entrustment_book() != null
					&& xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_belong_entrustment_book().trim().length() > 0) {
				xsjsglxt_check_entrustment_book = inspectionIdentificationDao
						.getCheckEntrustmentBookById(xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_belong_entrustment_book().trim());
				if (xsjsglxt_check_entrustment_book != null) {
					return xsjsglxt_check_entrustment_book.getXsjsglxt_check_entrustment_book_id();
				}
			}
		}
		return null;
	}

	/**
	 *
	 * 
	 * 方法
	 * 
	 * 
	 */
	// 不受理委托鉴定告知书
	public Map<String, Object> mapNotAcceptanceIdentifieder(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		// 主要获取委托书、以及不受理委托鉴定告知
		xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book = new xsjsglxt_check_entrustment_book();
		xsjsglxt_not_acceptance_entrustment_inform xsjsglxt_not_acceptance_entrustment_inform = new xsjsglxt_not_acceptance_entrustment_inform();
		// 获取不受理委托鉴定告知
		xsjsglxt_not_acceptance_entrustment_inform = inspectionIdentificationDao.getNotAcceptanceEntrustmentInformByOwnId(id);
		if (xsjsglxt_not_acceptance_entrustment_inform != null) {
			if (xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_belong_entrustment_book() != null
					&& xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_belong_entrustment_book().trim().length() > 0) {
				xsjsglxt_check_entrustment_book = inspectionIdentificationDao
						.getCheckEntrustmentBookById(xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_belong_entrustment_book().trim());
				// 我们先将委托书表中的拥有的数据替换进去
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim().length() > 0) {
					params.put("${n1}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim());
				} else {
					params.put("${n1}", "");
				}
				// 送检人
				String temp1 = "";
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim().length() > 0) {
					temp1 = temp1 + xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim();
				}
				String temp2 = "";
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim().length() > 0) {
					temp2 = temp2 + xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim();
				}
				params.put("${n2}", temp1 + "  " + temp2);
				// 送检人编号
				String tmp1 = "";
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number().trim().length() > 0) {
					tmp1 = tmp1 + xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number().trim();
				}
				String tmp2 = "";
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number().trim().length() > 0) {
					tmp2 = tmp2 + xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number().trim();
				}
				params.put("${n3}", "工作证：" + tmp1 + "  " + tmp2);
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim().length() > 0) {
					params.put("${n4}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim());
				} else {
					params.put("${n4}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num().trim().length() > 0) {
					params.put("${n5}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num().trim());
				} else {
					params.put("${n5}", "");
				}
				//
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim().length() > 0) {
					params.put("${n14}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(0, 4));
					params.put("${n15}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(4));
				} else {
					params.put("${n14}", "");
					params.put("${n15}", "");
				}
			}
			if (xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_reason() != null
					&& xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_reason().trim().length() > 0) {
				params.put("${n6}", xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_reason().trim() + "，故不予受理。");
			} else {
				params.put("${n6}", "");
			}
			if (xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_inputhuman_name() != null
					&& xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_inputhuman_name().trim().length() > 0) {
				params.put("${n7}", xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_inputhuman_name().trim());
			} else {
				params.put("${n7}", "");
			}
			if (xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_approvalhuman_name() != null
					&& xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_approvalhuman_name().trim().length() > 0) {
				params.put("${n8}", xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_approvalhuman_name().trim());
			} else {
				params.put("${n8}", "");
			}
			if (xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_approval_time() != null
					&& xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_approval_time().trim().length() > 0) {
				params.put("${n9}", xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_approval_time().trim());
			} else {
				params.put("${n9}", "");
			}
			if (xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_reason() != null
					&& xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_reason().trim().length() > 0) {
				params.put("${n12}", xsjsglxt_not_acceptance_entrustment_inform.getNot_acceptance_entrustment_inform_reason().trim());
			} else {
				params.put("${n12}", "");
			}
		}
		return params;
	}

	// 受 理 鉴 定 回 执
	public Map<String, Object> mapAcceptanceIdentifieder(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		// 主要获取的是委托书表、确认书表
		xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book = new xsjsglxt_check_entrustment_book();
		xsjsglxt_identifieder_case_confirm_book xsjsglxt_identifieder_case_confirm_book = new xsjsglxt_identifieder_case_confirm_book();
		// 获取确认书
		xsjsglxt_identifieder_case_confirm_book = inspectionIdentificationDao.getIdentifiederCaseConfirmBookByOwnId(id);
		if (xsjsglxt_identifieder_case_confirm_book != null) {
			if (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_belong_entrustment_book() != null
					&& xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_belong_entrustment_book().trim().length() > 0) {
				xsjsglxt_check_entrustment_book = inspectionIdentificationDao
						.getCheckEntrustmentBookById(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_belong_entrustment_book().trim());
				// 将属于委托书表中的数据替换进去
				// 第一个：委托日期
				// 因为再存储的时候,日期的格式一定一致,所以不会出现错误
				// 1.将日期转换成 年、月、日
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim().length() > 0) {
					params.put("${a1}", TeamUtil.timeToYear(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim()));
				} else {
					params.put("${a1}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim().length() > 0) {
					params.put("${a2}", TeamUtil.timeToMonth(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim()));
				} else {
					params.put("${a2}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim().length() > 0) {
					params.put("${a3}", TeamUtil.timeToDay(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim()));
				} else {
					params.put("${a3}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim().length() > 0) {
					params.put("${a4}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim());
				} else {
					params.put("${a4}", "");
				}
				String temp1 = "";
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim().length() > 0) {
					temp1 = temp1 + xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim();
				}
				String temp2 = "";
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim().length() > 0) {
					temp2 = temp2 + xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim();
				}
				params.put("${a5}", temp1 + "  " + temp2);
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim().length() > 0) {
					params.put("${a6}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim());
					params.put("${a7}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim());
				} else {
					params.put("${a6}", "");
					params.put("${a7}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim().length() > 0) {
					params.put("${a8}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(0, 4));
					params.put("${a9}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(4));
				} else {
					params.put("${a8}", "");
					params.put("${a9}", "");
				}
			}
			if (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_num() != null
					&& xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_num().trim().length() > 0) {
				params.put("${a10}", (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_num().trim()).substring(0, 4));
				params.put("${a11}", (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_num().trim()).substring(4));
			} else {
				params.put("${a10}", "");
				params.put("${a11}", "");
			}
			if (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_major() != null
					&& xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_major().trim().length() > 0) {
				params.put("${a12}", xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_major().trim());
			} else {
				params.put("${a12}", "");
			}
			if (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_human_name() != null
					&& xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_human_name().trim().length() > 0) {
				params.put("${a13}", xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_human_name().trim());
			} else {
				params.put("${a13}", "");
			}
			// 这个领取日期似乎不是那么好确定
			if (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_receive_data() != null
					&& xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_receive_data().trim().length() > 0) {
				params.put("${a14}", TeamUtil.timeToYear(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_receive_data().trim()));
				params.put("${a15}", TeamUtil.timeToMonth(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_receive_data().trim()));
				params.put("${a16}", TeamUtil.timeToDay(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_receive_data().trim()));
			} else {
				params.put("${a14}", "");
				params.put("${a15}", "");
				params.put("${a16}", "");
			}
		}
		return params;
	}

	// 鉴定确认书
	public Map<String, Object> mapIdentifiederCaseConfirmBook(String id) {
		xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book = new xsjsglxt_check_entrustment_book();
		xsjsglxt_identifieder_case_confirm_book xsjsglxt_identifieder_case_confirm_book = new xsjsglxt_identifieder_case_confirm_book();
		Map<String, Object> params = new HashMap<String, Object>();
		xsjsglxt_identifieder_case_confirm_book = inspectionIdentificationDao.getIdentifiederCaseConfirmBookByOwnId(id);
		if (xsjsglxt_identifieder_case_confirm_book != null) {
			if (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_belong_entrustment_book() != null
					&& xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_belong_entrustment_book().trim().length() > 0) {
				xsjsglxt_check_entrustment_book = inspectionIdentificationDao
						.getCheckEntrustmentBookById(xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_belong_entrustment_book().trim());
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit_name().trim().length() > 0) {
					params.put("${q3}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit_name().trim());
				} else {
					params.put("${q3}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim().length() > 0) {
					params.put("${q4}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim());
				} else {
					params.put("${q4}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim().length() > 0) {
					params.put("${q5}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(0, 4));
					params.put("${q6}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(4));
				} else {
					params.put("${q5}", "");
					params.put("${q6}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim().length() > 0) {
					params.put("${q7}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim());
				} else {
					params.put("${q7}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim().length() > 0) {
					params.put("${q10}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim());
				} else {
					params.put("${q10}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_duty() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_duty().trim().length() > 0) {
					params.put("${q8}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_duty().trim());
				} else {
					params.put("${q8}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_duty() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_duty().trim().length() > 0) {
					params.put("${q11}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_duty().trim());
				} else {
					params.put("${q11}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number().trim().length() > 0) {
					params.put("${q9}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number().trim());
				} else {
					params.put("${q9}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number().trim().length() > 0) {
					params.put("${q12}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number().trim());
				} else {
					params.put("${q12}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_communication_address() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_communication_address().trim().length() > 0) {
					params.put("${q13}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_communication_address().trim());
				} else {
					params.put("${q13}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_zip_code() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_zip_code().trim().length() > 0) {
					params.put("${q14}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_zip_code().trim());
				} else {
					params.put("${q14}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_phone() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_phone().trim().length() > 0) {
					params.put("${q15}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_phone().trim());
				} else {
					params.put("${q15}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_fax_num() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_fax_num().trim().length() > 0) {
					params.put("${q16}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_fax_num().trim());
				} else {
					params.put("${q16}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim().length() > 0) {
					params.put("${q17}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim());
				} else {
					params.put("${q17}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num().trim().length() > 0) {
					params.put("${q18}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num().trim());
				} else {
					params.put("${q18}", "");
				}
				if ("痕迹检验".equals(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_type())) {
					params.put("${q19}", "/");
					params.put("${q20}", "/");
					params.put("${q21}", "/");
					params.put("${q22}", "/");
					params.put("${q23}", "/");
					params.put("${q24}", "/");
				} else {
					if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_name() != null
							&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_name().trim().length() > 0) {
						params.put("${q19}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_name().trim());
					} else {
						params.put("${q19}", "");
					}
					if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_sex() != null
							&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_sex().trim().length() > 0) {
						params.put("${q20}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_sex().trim());
					} else {
						params.put("${q20}", "");
					}
					if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_age() != null
							&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_age().trim().length() > 0) {
						params.put("${q21}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_age().trim());
					} else {
						params.put("${q21}", "");
					}
					if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_phone() != null
							&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_phone().trim().length() > 0) {
						params.put("${q22}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_phone().trim());
					} else {
						params.put("${q22}", "");
					}
					if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit() != null
							&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim().length() > 0) {
						params.put("${q23}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim());
					} else {
						params.put("${q23}", "");
					}
					if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_address() != null
							&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_address().trim().length() > 0) {
						params.put("${q24}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_address().trim());
					} else {
						params.put("${q24}", "");
					}
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_simple_case_situation() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_simple_case_situation().trim().length() > 0) {
					params.put("${q25}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_simple_case_situation().trim());
				} else {
					params.put("${q25}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_sample_situation() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_sample_situation().trim().length() > 0) {
					params.put("${q26}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_sample_situation().trim());
				} else {
					params.put("${q26}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_request() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_request().trim().length() > 0) {
					params.put("${q27}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_request().trim());
				} else {
					params.put("${q27}", "");
				}
			}
			if (xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_human_name() != null
					&& xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_human_name().trim().length() > 0) {
				params.put("${q28}", xsjsglxt_identifieder_case_confirm_book.getIdentifieder_case_confirm_book_acceptance_human_name().trim());
			} else {
				params.put("${q28}", "");
			}
		}
		return params;
	}

	// 鉴定委托书Map
	public Map<String, Object> mapTranceCheckBook(String id) {
		xsjsglxt_check_entrustment_book xsjsglxt_check_entrustment_book = new xsjsglxt_check_entrustment_book();
		Map<String, Object> params = new HashMap<String, Object>();
		// 根据ID获取委托书
		xsjsglxt_check_entrustment_book = inspectionIdentificationDao.getCheckEntrustmentBookById(id);
		if (xsjsglxt_check_entrustment_book != null) {
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num() != null && xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim().length() > 0) {
				params.put("${x1}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(0, 4));
				params.put("${x2}", (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_num().trim()).substring(4));
			} else {
				params.put("${x1}", "");
				params.put("${x2}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim().length() > 0) {
				params.put("${x3}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim());
			} else {
				params.put("${x3}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim().length() > 0) {
				params.put("${x4}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspect_time().trim());
			} else {
				params.put("${x4}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim().length() > 0) {
				params.put("${x5}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_name().trim());
			} else {
				params.put("${x5}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim().length() > 0) {
				params.put("${x8}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_name().trim());
			} else {
				params.put("${x8}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_duty() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_duty().trim().length() > 0) {
				params.put("${x6}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_duty().trim());
			} else {
				params.put("${x6}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_duty() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_duty().trim().length() > 0) {
				params.put("${x9}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_duty().trim());
			} else {
				params.put("${x9}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number().trim().length() > 0) {
				params.put("${x7}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors1_jobcard_number().trim());
			} else {
				params.put("${x7}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number().trim().length() > 0) {
				params.put("${x10}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_inspectors2_jobcard_number().trim());
			} else {
				params.put("${x10}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_communication_address() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_communication_address().trim().length() > 0) {
				params.put("${x11}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_communication_address().trim());
			} else {
				params.put("${x11}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_zip_code() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_zip_code().trim().length() > 0) {
				params.put("${x12}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_zip_code().trim());
			} else {
				params.put("${x12}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_phone() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_phone().trim().length() > 0) {
				params.put("${x13}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_phone().trim());
			} else {
				params.put("${x13}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_fax_num() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_fax_num().trim().length() > 0) {
				params.put("${x14}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_fax_num().trim());
			} else {
				params.put("${x14}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit_name() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit_name().trim().length() > 0) {
				params.put("${x15}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit_name().trim());
			} else {
				params.put("${x15}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim().length() > 0) {
				params.put("${x16}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_name().trim());
			} else {
				params.put("${x16}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num().trim().length() > 0) {
				params.put("${x17}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_case_num().trim());
			} else {
				params.put("${x17}", "");
			}
			if ("痕迹检验".equals(xsjsglxt_check_entrustment_book.getCheck_entrustment_book_type())) {
				params.put("${x18}", "/");
				params.put("${x19}", "/");
				params.put("${x20}", "/");
				params.put("${x21}", "/");
				params.put("${x22}", "/");
				params.put("${x23}", "/");
			} else {
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_name() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_name().trim().length() > 0) {
					params.put("${x18}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_name().trim());
				} else {
					params.put("${x18}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_sex() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_sex().trim().length() > 0) {
					params.put("${x19}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_sex().trim());
				} else {
					params.put("${x19}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_age() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_age().trim().length() > 0) {
					params.put("${x20}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_age().trim());
				} else {
					params.put("${x20}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_phone() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_phone().trim().length() > 0) {
					params.put("${x21}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_phone().trim());
				} else {
					params.put("${x21}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim().length() > 0) {
					params.put("${x22}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_unit().trim());
				} else {
					params.put("${x22}", "");
				}
				if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_address() != null
						&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_address().trim().length() > 0) {
					params.put("${x23}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustmentor_address().trim());
				} else {
					params.put("${x23}", "");
				}
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_simple_case_situation() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_simple_case_situation().trim().length() > 0) {
				params.put("${x24}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_simple_case_situation().trim());
			} else {
				params.put("${x24}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_old_entrustment_situation() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_old_entrustment_situation().trim().length() > 0) {
				params.put("${x25}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_old_entrustment_situation().trim());
			} else {
				params.put("${x25}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_sample_situation() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_sample_situation().trim().length() > 0) {
				params.put("${x26}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_sample_situation().trim());
			} else {
				params.put("${x26}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_request() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_request().trim().length() > 0) {
				params.put("${x27}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_entrustment_request().trim());
			} else {
				params.put("${x27}", "");
			}
			if (xsjsglxt_check_entrustment_book.getCheck_entrustment_book_responsible_person() != null
					&& xsjsglxt_check_entrustment_book.getCheck_entrustment_book_responsible_person().trim().length() > 0) {
				params.put("${x28}", xsjsglxt_check_entrustment_book.getCheck_entrustment_book_responsible_person().trim());
			} else {
				params.put("${x28}", "");
			}
		}
		return params;
	}

	// 获取委托书状态
	public String entrustmentBookState(String id) {
		xsjsglxt_check_entrustment_book checkEntrustmentBook = new xsjsglxt_check_entrustment_book();
		checkEntrustmentBook = inspectionIdentificationDao.getCheckEntrustmentBookById(id);
		if (checkEntrustmentBook == null)
			return null;
		return checkEntrustmentBook.getCheck_entrustment_book_state();
	}

	// 获取委托书的鉴定要求
	public String entrustmentBookRequest(String id) {
		xsjsglxt_check_entrustment_book checkEntrustmentBook = new xsjsglxt_check_entrustment_book();
		checkEntrustmentBook = inspectionIdentificationDao.getCheckEntrustmentBookById(id);
		if (checkEntrustmentBook == null)
			return null;
		return checkEntrustmentBook.getCheck_entrustment_book_entrustment_request();
	}

	// 更改委托书状态
	public int updateEntrustmentBookState(String id, String state) {
		int i = 0;
		xsjsglxt_check_entrustment_book checkEntrustmentBook = new xsjsglxt_check_entrustment_book();
		// 根据ID获取委托书
		checkEntrustmentBook = inspectionIdentificationDao.getCheckEntrustmentBookById(id);
		if (checkEntrustmentBook != null) {
			checkEntrustmentBook.setCheck_entrustment_book_state(state);
			checkEntrustmentBook.setCheck_entrustment_book_gmt_modified(TeamUtil.getStringSecond());
			// 保存
			i = inspectionIdentificationDao.saveObject(checkEntrustmentBook);
		}
		return i;
	}

	// 返回委托书的类型
	public String entrustmentBookType(String id) {
		xsjsglxt_check_entrustment_book checkEntrustmentBook = new xsjsglxt_check_entrustment_book();
		checkEntrustmentBook = inspectionIdentificationDao.getCheckEntrustmentBookById(id);
		if (checkEntrustmentBook != null) {
			return checkEntrustmentBook.getCheck_entrustment_book_type();
		}
		return null;
	}

	// 上传损伤检验记录照片
	public xsjsglxt_damage_inspection_record uploadDamage(String uploadName, File file, String oldFileName, xsjsglxt_damage_inspection_record damageInspectionRecord,
			String newFileName) throws IOException {
		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}
		String path = lj + "xsjsglxt/damage/";
		// 如果新文件为空
		if (file == null) {
			// 判断旧文件是否处于空的状态
			// 这里的旧文件是指在数据库中自己查出来的原文件名
			if (oldFileName != null && oldFileName.trim().length() > 0) {
				// 如果旧文件存在,则进行操作
				// 删除旧文件
				path = path + damageInspectionRecord.getXsjsglxt_damage_inspection_record_id() + "_" + oldFileName;
				File deleteFile = new File(path);
				deleteFile.delete();
				switch (uploadName) {
				case "损伤检验图1":
					damageInspectionRecord.setDamage_inspection_record_picture1("");
					break;
				case "损伤检验图2":
					damageInspectionRecord.setDamage_inspection_record_picture2("");
					break;
				case "损伤检验图3":
					damageInspectionRecord.setDamage_inspection_record_picture3("");
					break;
				}
				return damageInspectionRecord;
			} else {
				// 旧文件如果为空,则不用进行任何操作
				return damageInspectionRecord;
			}
		} else {
			// 如果新文件存在
			// 判断旧文件是否存在
			if (oldFileName != null && oldFileName.trim().length() > 0) {
				// 如果旧文件存在
				// 删除旧文件
				path = path + damageInspectionRecord.getXsjsglxt_damage_inspection_record_id() + "_" + oldFileName;
				File deleteFile = new File(path);
				deleteFile.delete();
			}
			// 直接进行上传以及存入
			// 上传文件
			path = lj + "xsjsglxt/damage/";
			path = path + damageInspectionRecord.getXsjsglxt_damage_inspection_record_id() + "_" + newFileName;
			File newFile = new File(path);
			FileUtils.copyFile(file, newFile);
			// 改变字段名
			switch (uploadName) {
			case "损伤检验图1":
				damageInspectionRecord.setDamage_inspection_record_picture1(newFileName);
				break;
			case "损伤检验图2":
				damageInspectionRecord.setDamage_inspection_record_picture2(newFileName);
				break;
			case "损伤检验图3":
				damageInspectionRecord.setDamage_inspection_record_picture3(newFileName);
				break;
			}
			return damageInspectionRecord;
		}
	}

	// 上传尸体检验记录表图片
	// oldFileName需要供给
	// uploadName 需要供给
	public xsjsglxt_death_inspection_record uploadDeath(String uploadName, File file, String oldFileName, xsjsglxt_death_inspection_record deathInspectionRecord,
			String newFileName) throws IOException {
		/*
		 * 获取路径
		 */
		String lj = "";
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream("file.properties"));
			lj = props.getProperty("lj");
		} catch (Exception e) {
			System.out.println("获取初始路径失败");
			e.printStackTrace();
		}
		String path = lj + "xsjsglxt/death/";
		// 如果新文件为空
		if (file == null) {
			// 判断旧文件是否处于空的状态
			// 这里的旧文件是指在数据库中自己查出来的原文件名
			if (oldFileName != null && oldFileName.trim().length() > 0) {
				// 如果旧文件存在,则进行操作
				// 删除旧文件
				path = path + deathInspectionRecord.getXsjsglxt_death_inspection_record_id() + "_" + oldFileName;
				File deleteFile = new File(path);
				deleteFile.delete();
				switch (uploadName) {
				case "尸表检验图1":
					deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture1("");
					break;
				case "尸表检验图2":
					deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture2("");
					break;
				case "尸表检验图3":
					deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture3("");
					break;
				case "解剖检验图1":
					deathInspectionRecord.setDeath_inspection_record_anatomy_picture1("");
					break;
				case "解剖检验图2":
					deathInspectionRecord.setDeath_inspection_record_anatomy_picture2("");
					break;
				case "解剖检验图3":
					deathInspectionRecord.setDeath_inspection_record_anatomy_picture3("");
					break;
				}
				return deathInspectionRecord;
			} else {
				// 旧文件如果为空,则不用进行任何操作
				return deathInspectionRecord;
			}
		} else {
			// 如果新文件存在
			// 判断旧文件是否存在
			if (oldFileName != null && oldFileName.trim().length() > 0) {
				// 如果旧文件存在
				// 删除旧文件
				path = path + deathInspectionRecord.getXsjsglxt_death_inspection_record_id() + "_" + oldFileName;
				File deleteFile = new File(path);
				deleteFile.delete();
			}
			// 直接进行上传以及存入
			// 上传文件
			path = lj + "xsjsglxt/death/";
			path = path + deathInspectionRecord.getXsjsglxt_death_inspection_record_id() + "_" + newFileName;
			File newFile = new File(path);
			FileUtils.copyFile(file, newFile);
			// 存储到数据库
			switch (uploadName) {
			case "尸表检验图1":
				deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture1(newFileName);
				break;
			case "尸表检验图2":
				deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture2(newFileName);
				break;
			case "尸表检验图3":
				deathInspectionRecord.setDeath_inspection_record_autopsy_table_test_picture3(newFileName);
				break;
			case "解剖检验图1":
				deathInspectionRecord.setDeath_inspection_record_anatomy_picture1(newFileName);
				break;
			case "解剖检验图2":
				deathInspectionRecord.setDeath_inspection_record_anatomy_picture2(newFileName);
				break;
			case "解剖检验图3":
				deathInspectionRecord.setDeath_inspection_record_anatomy_picture3(newFileName);
				break;
			}
			return deathInspectionRecord;
		}
	}

	/**
	 * 
	 * @param oldFile
	 * @param p
	 * @D 表示某个位置为空
	 * @D String[] p = {"1","1","1","1","1","1"};
	 * @D p = {"1","2","1","2","1","2"} 表示位置：1，3，5 为空.1 表示存在文件
	 * @return
	 */
	public File[] file(File[] oldFile, String[] p, String type) {
		int h = 0;
		if ("death".equals(type)) {
			h = 6;
		} else {
			h = 3;
		}
		File[] file = new File[h];
		for (int i = 0, k = 0; i < p.length; i++) {
			if ("1".equals(p[i])) {
				file[i] = oldFile[k];
				k++;
			} else {
				file[i] = null;
			}
		}
		return file;
	}

	/**
	 * 整合文件名
	 * 
	 * @param oldFileName
	 * @param p
	 * @return
	 */
	public String[] fileName(String[] oldFileName, String[] p, String type) {
		int h = 0;
		if ("death".equals(type)) {
			h = 6;
		} else {
			h = 3;
		}
		String[] fileName = new String[h];
		for (int i = 0, k = 0; i < p.length; i++) {
			if ("1".equals(p[i])) {
				fileName[i] = oldFileName[k];
				k++;
			} else {
				fileName[i] = null;
			}
		}
		return fileName;
	}

}
