package com.IttalentsHomeworks.DAO;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.IttalentsHomeworks.DB.DBManager;

@Configuration
@ComponentScan
public class ConfigAutowire {

	@Bean
	public DBManager manager(){
		return new DBManager();
	}
	@Bean 
	public UserDAO userdao(){
		UserDAO d = new UserDAO();
		System.out.println("Will return " + d);
		return d;
	}
	@Bean 
	public GroupDAO groupdao(){
		return new GroupDAO();
	}
	@Bean 
	public ValidationsDAO validationsdao(){
		return new ValidationsDAO();
	}
}
