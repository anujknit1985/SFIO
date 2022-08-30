package com.snms.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class EsignDTO {
	private String name;
	private String username;
	private String clientrequestURL;
	private MultipartFile userImage;
	private String document;
	private String xml;

}
