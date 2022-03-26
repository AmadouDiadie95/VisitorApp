package com.radisson.collection.security;

import java.util.Calendar;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	/*
	 * private String[] listLicense = {"1234",""} ; private Boolean existedLicense =
	 * false ;
	 * 
	 * @Bean public void TestLicense() { // TODO Auto-generated method stub for (int
	 * i = 0; i < listLicense.length; i++) { if ("12" == listLicense[i]) {
	 * existedLicense = true ; } } if (existedLicense == true) {
	 * System.out.println("License is True"); } else {
	 * System.out.println("License is False"); }
	 * 
	 * }
	 */
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		auth.inMemoryAuthentication().withUser("admin").password("{noop}Koira@1234").roles("ADMIN") ;
		}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		
		Calendar c = Calendar.getInstance() ;
		int year = c.get(Calendar.YEAR) ;
		int mouth = c.get(Calendar.MONTH) + 1 ;
		int day = c.get(Calendar.DAY_OF_MONTH) ;
		 
//		if (year > 2021 && mouth >= 3) {
//			http.authorizeRequests().antMatchers("/","/index","/visiteurs","/anciens","/searchZone").denyAll() ;
//		} else {
//			http.formLogin();
//			http.authorizeRequests().antMatchers("/delete**/**", "/export**/**", "/modifProfile**/**").hasRole("ADMIN") ;
//		}	
//		 http.httpBasic();
//		if (existedLicense == true ) {
//			http.authorizeRequests().antMatchers("/delete***", "/export**/**", "/modifProfile**/**").hasRole("ADMIN") ;
//		}else  
//
//		{
//			http.authorizeRequests().antMatchers("/visiteurs","/anciens").denyAll() ;
//			http.authorizeRequests().antMatchers("/searchZone").hasRole("ADMIN") ;
//			http.exceptionHandling().accessDeniedPage("/LicensePage") ;
//		}
	}
	
	

}
