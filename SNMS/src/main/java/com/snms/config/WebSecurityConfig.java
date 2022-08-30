package com.snms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.snms.controllers.MethodFilter;
import com.snms.service.CustomAuthenticationProvider;
import com.snms.service.CustomSuccessHandler;
import com.snms.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;
	
	@Autowired
	private CustomSuccessHandler customSuccessHandler; 

	
	@Autowired
	private MethodFilter methodFilter;
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	    MappingJackson2HttpMessageConverter converter = 
	        new MappingJackson2HttpMessageConverter(mapper);
	    return converter;
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(customAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//http.csrf().disable();
		http.csrf().ignoringAntMatchers("/hello1","/respNotice","/respSummon","/details","/ApprovedpersonDetails","/personDetails","/personcompanyList","/ApprovedpersonDetailsBy","/updateInGep","/getInvestigationcaseList","/getProsecutioncaseList","/getTotalcaseList","/showpersonAddedincomp","/getTotalCUIList","/getpanDetails","/getValidEmail","/getValidPan","getValidPersonEmail","/getApprovedPersonCount","/getApprovedPersonCountByDate","/showmcaOrderDetails","/searchmcaOrder","/showInvcomplist","/showInvKmplist","/getValidCompOrder");
		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
		http.sessionManagement().maximumSessions(1).expiredUrl("/login");
		http.sessionManagement().sessionFixation().migrateSession().invalidSessionUrl("/login");
	// http.sessionManagement().sessionFixation().newSession();
		http.headers().frameOptions().sameOrigin();
		
		
		
		http.authorizeRequests().antMatchers("/","/index","/captcha","/forgotPass","/getForgotOtp","/forgotPassword","/saveForgotPass","/getUniqueEmail","/caseId/personName","/caseId/personDetails","/caseId/ApprovedpersonDetails","/caseId/personcompanyList","/caseId/ApprovedpersonDetailsBy","/caseId/updateInGep","/caseId/getInvestigationcaseList","/caseId/getProsecutioncaseList","/caseId/getTotalcaseList","/caseId/showpersonAddedincomp","/caseId/getTotalCUIList","/hello1","/respNotice","/respSummon","/caseId/getApprovedPersonCount","/caseId/getApprovedPersonCountByDate","/mcaOrderDtl/showmcaOrderDetails","/mcaOrderDtl/searchmcaOrder","/mcaOrderDtl/showInvcomplist","/mcaOrderDtl/showInvKmplist","/getValidCompOrder","/genSummonAs1","/editUser1","/PdfSummonAs1","/genNoticeAs1","/genOfflineSummonAs1","/PdfNoticeAs1","/PdfOfflineSummonAs1").permitAll()
				.antMatchers("/login","/passHashing","/students/**").permitAll()
				//.antMatchers("/unsignPdf","/generateHash","/hello1","/respNotice","/respSummon").hasAnyRole("USER","DIRECTOR")
				.antMatchers("/unsignPdf","/generateHash").hasAnyRole("USER","DIRECTOR")
				.antMatchers("/user/**").hasAuthority("ROLE_USER")
				.antMatchers("/userHome").hasAuthority("ROLE_USER")
				.antMatchers("/home").hasAuthority("ROLE_ADMIN")
				.antMatchers("/dirHome").hasAuthority("ROLE_DIRECTOR")
				.antMatchers("/adsHome").hasAuthority("ROLE_ADMIN_SECTION")
				.antMatchers("/adoHome").hasAuthority("ROLE_ADMIN_OFFICER")
				.antMatchers("/showCert","/getCertList","/saveCert","/successReg","/unregDSC","/viewpdffile").hasAnyRole("DIRECTOR","USER")
				.antMatchers("/approvedOrders","/esignOrder","/savepdf").hasRole("DIRECTOR").antMatchers("/getApprovedOrder").hasAnyRole("ADMIN_OFFICER","DIRECTOR")
				.antMatchers("/esignNotice","/savenoticePdf","/esignSummon","/savesummonPdf").hasRole("USER")
				.antMatchers("/approvedNotice","/getApprovedNotice","/approvedSummon","/getApprovedSummons").hasAnyRole("USER","DIRECTOR")
				.anyRequest().authenticated()
				.and().formLogin().loginPage("/login")
				.failureUrl("/login?error=true")
				.loginProcessingUrl("/securityCheck").loginPage("/login")
				.successHandler(customSuccessHandler).permitAll()
				.and()
				.logout()
				//.logoutSuccessUrl("/login?logout")
				.logoutUrl("/logout").deleteCookies("JSESSIONID").invalidateHttpSession(true)
				.and().exceptionHandling().accessDeniedPage("/error403");
		
		
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "plugins**",
				"fonts**","/.well-known/**");
	}
	
}
