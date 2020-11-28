package com.ctsi.zz.config;

import com.ctsi.ssdc.security.AuthoritiesConstants;
import com.ctsi.ssdc.security.jwt.JWTConfigurer;
import com.ctsi.ssdc.security.jwt.TokenProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private CorsFilter corsFilter;
	@Autowired
	private SecurityProblemSupport problemSupport;

	@Bean
	public AccessDecisionManager accessDecisionManager() {
		RoleVoter voter = new RoleVoter();
		voter.setRolePrefix(StringUtils.EMPTY);

		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(new WebExpressionVoter(), voter,
				new AuthenticatedVoter());

		return new UnanimousBased(decisionVoters);
	}

	// 可在此添加自定义AuthenticationProvider（可参照CasAuthenticationProvider）
	/*@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

	}*/

	@Override
	public void configure(WebSecurity web) throws Exception {
//		web.ignoring()
//			.antMatchers(HttpMethod.OPTIONS, "/**")
//			.antMatchers("/swagger-ui/*")
//			.antMatchers("/api/system/login")
//			.antMatchers("/api/system/loginByCas");
//		关闭后端项目的安全验证
		//接口不过滤
		web.ignoring().antMatchers("/**");

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
		
			.exceptionHandling().authenticationEntryPoint(problemSupport)
			
			.accessDeniedHandler(problemSupport)
			.and()
				.csrf().disable()
				// .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
				.headers().frameOptions().sameOrigin()
			.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.apply(securityConfigurerAdapter())
			.and()
				.authorizeRequests()
				.accessDecisionManager(accessDecisionManager())

				.antMatchers("/v2/api-docs/**").permitAll()
				.antMatchers("/swagger-resources/configuration/ui").permitAll()
				.antMatchers("/swagger-ui/*").permitAll()
				
				// 获取验证码
				.antMatchers("/api/catpcha/digitalCaptcha").permitAll()

				.antMatchers("/api/system/login").permitAll()
				.antMatchers("/api/system/refreshToken").authenticated()
				.antMatchers("/api/system/cscpMenus").authenticated()
				.antMatchers("/api/system/cscpCurrentUserDetails").authenticated()

			    .antMatchers("/api/system/cscpUserDetailsByUserId").authenticated()
				.antMatchers("/api/system/cscpUserPassword").authenticated()

				.antMatchers(HttpMethod.GET, "/api/system/cscpRolessByCriteria").authenticated()
				.antMatchers(HttpMethod.PUT, "/api/system/cscpUserDetails").authenticated()

				.antMatchers(HttpMethod.GET, "/api/system/cscpUserDetailsOr")
					.hasAnyAuthority(AuthoritiesConstants.USER_QUERY, AuthoritiesConstants.USER_EDIT, AuthoritiesConstants.WG_EDIT,
							AuthoritiesConstants.WG_ADD, AuthoritiesConstants.ORG_EDIT)
				.antMatchers(HttpMethod.POST, "/api/system/cscpUserDetails").hasAuthority(AuthoritiesConstants.USER_ADD)
				.antMatchers(HttpMethod.DELETE, "/api/system/cscpUsers/*").hasAuthority(AuthoritiesConstants.USER_DEL)
				.antMatchers(HttpMethod.GET, "/api/system/cscpUserExistByUsername").hasAuthority(AuthoritiesConstants.USER_ADD)

				.antMatchers(HttpMethod.GET,"/api/system/cscpRoless/*").hasAnyAuthority(AuthoritiesConstants.ROLE_QUERY,AuthoritiesConstants.ROLE_ADD)
				.antMatchers(HttpMethod.POST,"/api/system/menu/queryByRoleId").hasAuthority(AuthoritiesConstants.ROLE_ADD)
				.antMatchers(HttpMethod.POST,"/api/system/cscpRoless").hasAuthority(AuthoritiesConstants.ROLE_ADD)
				.antMatchers(HttpMethod.POST,"/api/system/menu/save").hasAuthority(AuthoritiesConstants.ROLE_ADD)					
				.antMatchers(HttpMethod.PUT,"/api/system/cscpRoless").hasAuthority(AuthoritiesConstants.ROLE_EDIT)
				.antMatchers(HttpMethod.DELETE,"/api/system/cscpRoless/*").hasAuthority(AuthoritiesConstants.ROLE_DEL)
					
				.antMatchers(HttpMethod.GET,"/api/system/cscpWorkGroupsOr").hasAnyAuthority(AuthoritiesConstants.WG_QUERY, AuthoritiesConstants.ORG_EDIT)
				.antMatchers(HttpMethod.GET,"/api/system/cscpWorkGroups/*").hasAnyAuthority(AuthoritiesConstants.WG_QUERY,AuthoritiesConstants.WG_EDIT)
				.antMatchers(HttpMethod.PUT,"/api/system/cscpWorkGroups").hasAuthority(AuthoritiesConstants.WG_EDIT)
				.antMatchers(HttpMethod.DELETE,"/api/system/cscpWorkGroups/*").hasAuthority(AuthoritiesConstants.WG_DEL)
				.antMatchers(HttpMethod.POST,"/api/system/cscpWorkGroups").hasAuthority(AuthoritiesConstants.WG_ADD)

				.antMatchers(HttpMethod.GET, "/api/system/cscpOrgs/all").hasAuthority(AuthoritiesConstants.ORG_QUERY)
				.antMatchers(HttpMethod.POST, "/api/system/cscpOrgs/save").hasAuthority(AuthoritiesConstants.ORG_EDIT)

				.antMatchers(HttpMethod.GET, "/api/system/cscpLogLoginsByCriteria").hasAuthority(AuthoritiesConstants.LOGGING_LOGIN)
				.antMatchers(HttpMethod.GET, "/api/system/cscpLogOperationsByCriteria").hasAuthority(AuthoritiesConstants.LOGGING_OPERATION)
				.antMatchers("/api/system/cscpUserPassword").permitAll()
				.antMatchers("/api/**").authenticated();

	}

	// 添加token拦截校验的过滤器
	private JWTConfigurer securityConfigurerAdapter() {
		return new JWTConfigurer(tokenProvider);
	}

}
