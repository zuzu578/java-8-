package egovframework.com.cmm.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egovframe.rte.fdl.security.userdetails.util.EgovUserDetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import egovframework.com.bo.util.ReturnMessage;
import egovframework.com.bo.util.StringUtil;
import egovframework.com.cmm.LoginVO;

import egovframework.com.uat.role.service.EgovAuthManageService;
import egovframework.com.uat.role.service.RoleAuthVO;
import egovframework.com.uat.role.service.RoleUrlPatternVO;
import lombok.extern.slf4j.Slf4j;
/**
 * TODO : 로그인한 유저 권한에 따라 메뉴 접근권한 분기 
 *  스프링 시큐리티 인가 후 세션에 저장된 유저정보 를 통해 유저 권한을 가져온다.
	SecurityContext context = SecurityContextHolder.getContext();
	Authentication authentication = context.getAuthentication();
	String id = authentication.getName(); // 시큐리티에 잡힘 
 * @author dev002
 */


@Slf4j
public class MenuInterceptor extends HandlerInterceptorAdapter{
	
	private EgovAuthManageService egovAuthManageService;
	
	@Autowired
	public MenuInterceptor (EgovAuthManageService egovAuthManageService) {
		this.egovAuthManageService = egovAuthManageService;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean isAccess = false;
		boolean isPassed = false;
		
		String servletPath = request.getServletPath();
		
		try {
			
			LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
			RoleAuthVO auth = egovAuthManageService.getUserAuthority(loginVO);
			String authority = StringUtil.nvl(auth.getAuthority(), "");
	
			validate : {
				if(authority.equals("")) {
					response.getWriter().write("CODE :"+ReturnMessage.NOAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOAUTH.getMessage()+"");			
					isPassed = false;
					break validate;
				}else {
					isPassed = true;
				}
			}
			if(isPassed) {
				// 가져온 해당 유저의 권한을 바탕으로 롤 접근 url 을 가져온다.
				List<RoleUrlPatternVO> roleAuthUrlList = (List<RoleUrlPatternVO>)(Object)egovAuthManageService.getRoleAuthUrlListByauthority(authority);	
				// 접근 권한을 판별한다.	
				List<Object> result = roleAuthUrlList
						.stream()
						.filter(item -> servletPath.contains(item.getRolePttrn().toString().trim()))
						.collect(Collectors.toList())
						;
				
				if(result.isEmpty()) {
					log.debug("CODE :"+ReturnMessage.NOACCESSAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOACCESSAUTH.getMessage()+"");
					response.getWriter().write("CODE :"+ReturnMessage.NOACCESSAUTH.getCode() +" MESSAGE : "+ReturnMessage.NOACCESSAUTH.getMessage()+"");
				}else {
					isAccess = true;
				}
			}
			
		}catch(Exception e) {
			log.debug(e.getMessage());
		}
		return isAccess;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		log.debug("access success!!");
		
	}
}
