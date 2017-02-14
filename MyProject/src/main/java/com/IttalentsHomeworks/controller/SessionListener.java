package com.IttalentsHomeworks.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.http.HttpRequest;
 
public class SessionListener implements HttpSessionListener {
 
	@Override
	public void sessionCreated(HttpSessionEvent event) {
     //   event.getSession().setMaxInactiveInterval(12);
		event.getSession().setAttribute("sessionId", event.getSession().getId());
		System.out.println("A new session is created " + LocalDateTime.now() + "id of session: " + event.getSession().getId());
	}
 
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		//event.getSession().invalidate();
		//event.getSession().removeAttribute("user");
		//event.getSession().invalidate();
		//System.out.println(event.getSession().getAttribute("user") == null);
		//event.getSession().setAttribute("isSessionDestroyed", true);
		System.out.println("session is destroyed " + LocalDateTime.now() + "id of session: " + event.getSession().getId());
	}
   /*@Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("==== Session is created ====" + LocalDateTime.now());
        event.getSession().setMaxInactiveInterval(10);
    }
 
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        System.out.println("==== Session is destroyed ====");
        HttpServletResponse resp = null;
        try {
			resp.sendRedirect("/index");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
    }*/
	
}