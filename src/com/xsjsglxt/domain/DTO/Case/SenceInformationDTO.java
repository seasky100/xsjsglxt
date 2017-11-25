package com.xsjsglxt.domain.DTO.Case;

import com.xsjsglxt.domain.DO.xsjsglxt_briefdetails;
import com.xsjsglxt.domain.DO.xsjsglxt_case;
import com.xsjsglxt.domain.DO.xsjsglxt_lost;
import com.xsjsglxt.domain.DO.xsjsglxt_lost_computer;
import com.xsjsglxt.domain.DO.xsjsglxt_lost_mobilephone;
import com.xsjsglxt.domain.DO.xsjsglxt_picture;
import com.xsjsglxt.domain.DO.xsjsglxt_resevidence;
import com.xsjsglxt.domain.DO.xsjsglxt_snece;

public class SenceInformationDTO {
private xsjsglxt_briefdetails briefdetails;
private xsjsglxt_case Case;
private xsjsglxt_lost_computer lost_computer;
private xsjsglxt_lost_mobilephone lost_mobilephone;
private xsjsglxt_lost lost;
private xsjsglxt_picture picture ;
private xsjsglxt_snece sence;
private xsjsglxt_resevidence resevidence;

public xsjsglxt_resevidence getResevidence() {
	return resevidence;
}
public void setResevidence(xsjsglxt_resevidence resevidence) {
	this.resevidence = resevidence;
}
public xsjsglxt_briefdetails getBriefdetails() {
	return briefdetails;
}
public void setBriefdetails(xsjsglxt_briefdetails briefdetails) {
	this.briefdetails = briefdetails;
}
public xsjsglxt_case getCase() {
	return Case;
}
public void setCase(xsjsglxt_case case1) {
	Case = case1;
}
public xsjsglxt_lost_computer getLost_computer() {
	return lost_computer;
}
public void setLost_computer(xsjsglxt_lost_computer lost_computer) {
	this.lost_computer = lost_computer;
}
public xsjsglxt_lost_mobilephone getLost_mobilephone() {
	return lost_mobilephone;
}
public void setLost_mobilephone(xsjsglxt_lost_mobilephone lost_mobilephone) {
	this.lost_mobilephone = lost_mobilephone;
}
public xsjsglxt_lost getLost() {
	return lost;
}
public void setLost(xsjsglxt_lost lost) {
	this.lost = lost;
}
public xsjsglxt_picture getPicture() {
	return picture;
}
public void setPicture(xsjsglxt_picture picture) {
	this.picture = picture;
}
public xsjsglxt_snece getSence() {
	return sence;
}
public void setSence(xsjsglxt_snece sence) {
	this.sence = sence;
}

public SenceInformationDTO(xsjsglxt_briefdetails briefdetails, xsjsglxt_case case1,
		xsjsglxt_lost_computer lost_computer, xsjsglxt_lost_mobilephone lost_mobilephone, xsjsglxt_lost lost,
		xsjsglxt_picture picture, xsjsglxt_snece sence, xsjsglxt_resevidence resevidence) {

	this.briefdetails = briefdetails;
	Case = case1;
	this.lost_computer = lost_computer;
	this.lost_mobilephone = lost_mobilephone;
	this.lost = lost;
	this.picture = picture;
	this.sence = sence;
	this.resevidence = resevidence;
}
@Override
public String toString() {
	return "SenceInformationDTO [briefdetails=" + briefdetails + ", Case=" + Case + ", lost_computer=" + lost_computer
			+ ", lost_mobilephone=" + lost_mobilephone + ", lost=" + lost + ", picture=" + picture + ", sence=" + sence
			+ ", resevidence=" + resevidence + "]";
}


}
