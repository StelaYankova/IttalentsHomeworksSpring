package com.IttalentsHomeworks.controller;
import java.time.LocalDateTime;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
 
public class SessionListener implements HttpSessionListener {
 
	private static final int MAX_INACTIVE_INTERVAL = 10*60;

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		event.getSession().setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
		event.getSession().setAttribute("sessionId", event.getSession().getId());
		System.out.println("A new session is created " + LocalDateTime.now() + "id of session: " + event.getSession().getId());
	}
 
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		System.out.println("session is destroyed " + LocalDateTime.now() + "id of session: " + event.getSession().getId());
	}
	
}