package com.xsjsglxt.service.Technology;

import java.util.List;

import com.xsjsglxt.domain.DO.xsjsglxt_contrast_fingerprint;

public interface ContrastFingerPrintService {

	int saveDNA(xsjsglxt_contrast_fingerprint contrastFingerPrint);

	int deleteContrast(String contrast_fingerprint_id);

	int deleteListContrast(List<String> listDeleteContrastID);

	int modifiedContrast(xsjsglxt_contrast_fingerprint contrastFingerPrint);

	xsjsglxt_contrast_fingerprint get_contrast(String contrast_fingerprint_id);

}
