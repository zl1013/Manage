package com.ctsi.zz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ctsi.ssdc.captcha.CaptchaHandlerInterceptor;

//@Configuration
public class SpringMvcConfiguration implements WebMvcConfigurer  {

	@Bean
	public CaptchaHandlerInterceptor captchaHandlerInterceptor() {
		return new CaptchaHandlerInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry){

		WebMvcConfigurer.super.addInterceptors(registry);
		registry.addInterceptor(captchaHandlerInterceptor());

	}
}
