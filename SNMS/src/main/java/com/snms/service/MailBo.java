package com.snms.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.snms.dto.MailInfo;

@Service
public interface MailBo
{
    void sendMail(MailInfo info, int messageType, HttpServletRequest req) throws Exception;

	/*void sendMessage(String obj, int smsVarifyOtp,String mobileno) throws Exception;
	
	String getAutoEmailTemplate(MailInfo info, int messageType);*/
}
