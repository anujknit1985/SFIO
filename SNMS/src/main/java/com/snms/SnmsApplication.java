package com.snms;

import java.util.Properties;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.snms.utils.SaltGenerator;
import com.snms.utils.Utils;

import app.eoffice.dsc.service.DscService;

@SpringBootApplication
public class SnmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnmsApplication.class, args);
	}
	
	@Bean(name="dscService")
	public DscService dscService() {
		DscService dscService = new DscService();
		return dscService;
	}

	/*
	 * @Bean(name="captchaBean") //@SessionScope public CaptchaBean captchaBean() {
	 * CaptchaBean captchaBean=new CaptchaBean(180, 45); //width,height
	 * //captchaBean.setAddBorder(true); captchaBean.setTxtProd(new
	 * DefaultTextProducer(6, new
	 * String("123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").
	 * toCharArray())); return captchaBean; }
	 */
	@Bean(name = "saltGen")
	public SaltGenerator getSaltGen() {
		return new SaltGenerator();
	}
	
	 @Bean(name="mailSender")
	 public JavaMailSender mailSender()
	 {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setProtocol("smtp");
		mailSender.setPort(25);
		mailSender.setHost("relay.nic.in");
		mailSender.setUsername("anuj.kr@nic.in");
		mailSender.setPassword("#Tuktuk1");
		Properties prop = new Properties();
			
		prop.put("mail.transport.protocol", "smtp");
		/*
		 * prop.put("mail.smtp.auth", true); prop.put("mail.smtp.starttls.enable",
		 * true); prop.put("mail.smtp.socketFactory.class",
		 * "javax.net.ssl.SSLSocketFactory");
		 * prop.put("mail.smtp.socketFactory.fallback", false); prop.put("mail.debug",
		 * true); prop.put("mail.smtp.quitwait", false);
		 */
			
		mailSender.setJavaMailProperties(prop);
		return mailSender;
	}
		
	@Bean(name="emailConfig")
	public SimpleMailMessage emailConfig(){
		SimpleMailMessage emailConfig =  new SimpleMailMessage();
		emailConfig.setFrom("anuj.kr@nic.in");
		return emailConfig;
	}
		
	@Bean(name="utils")
	public Utils utils(){
		Utils utils = new Utils();
		return utils;
	}
	/*
	 * @Bean public ConfigurableServletWebServerFactory webServerFactory() {
	 * TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
	 * factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
	 * 
	 * @Override public void customize(Connector connector) {
	 * connector.setProperty("relaxedQueryChars", "|{}[]()/%"); } }); return
	 * factory; }
	 */
	/*
	 * @Bean public MultipartResolver multipartResolver() { CommonsMultipartResolver
	 * multipartResolver = new CommonsMultipartResolver();
	 * multipartResolver.setMaxUploadSize(5097152); return multipartResolver; }
	 */
}
