package com.IttalentsHomeworks.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class LoginFilter implements Filter {

    private static final int IS_IMAGE_END = 16;
	private static final int IS_IMAGE_BEG = 10;
	protected static final int UNAUTORIZED_STATUS_CODE = 401;

	@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {    
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String loginURI = request.getContextPath() + "/index";
        String loginURI3 = request.getContextPath() + "/LoginServlet";
        String loginURI4 = request.getContextPath() + "/RegisterServlet";
        String loginURI5 = request.getContextPath() + "/ValidateLogin";
        String loginURI6 = request.getContextPath() + "/IsUsernameUniqueServlet";
        String loginURI7 = request.getContextPath() + "/IsUsernameValid";
        String loginURI8 = request.getContextPath() + "/IsPasswordValid";
        
        boolean loggedIn = session != null && session.getAttribute("user") != null;
        boolean loginRequest = request.getRequestURI().equals(loginURI);
        boolean loginRequest1 = request.getRequestURI().equals(loginURI3);
        boolean loginRequest2 = request.getRequestURI().equals(loginURI4);
        boolean loginRequest5 = request.getRequestURI().equals(loginURI5);
        boolean loginRequest6 = request.getRequestURI().equals(loginURI6);
        boolean loginRequest7 = request.getRequestURI().equals(loginURI7);
        boolean loginRequest8 = request.getRequestURI().equals(loginURI8);
        boolean isImage = false;
        boolean isStylesheet = false;
        
        if(request.getRequestURI().length() >= 5){
        	//System.out.println(request.getRequestURL());
            System.out.println(request.getRequestURI().substring(request.getRequestURI().length()-4, request.getRequestURI().length()));

        	//isStylesheet = request.getRequestURI().substring(request.getRequestURI().length()-4, request.getRequestURI().length()).equals(".css");
        	isStylesheet = request.getRequestURI().contains(".css");
        }
        if(request.getRequestURI().length() >= 16){
        	isImage = request.getRequestURI().substring(IS_IMAGE_BEG, IS_IMAGE_END).equals("/image");
        }
		if (loggedIn || loginRequest || loginRequest2 || loginRequest1 || loginRequest5 || loginRequest6 || loginRequest7 || loginRequest8 || isImage || isStylesheet) {
			chain.doFilter(request, response);
		} else {
			if(!isAjax((HttpServletRequest) req)){
				response.sendRedirect(loginURI);
			}else{
				response.setStatus(UNAUTORIZED_STATUS_CODE);
			}
		}
	}
    public  boolean isAjax(HttpServletRequest request) {
		   return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}