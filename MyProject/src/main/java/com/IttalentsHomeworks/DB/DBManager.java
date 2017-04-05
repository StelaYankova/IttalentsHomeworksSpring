package com.IttalentsHomeworks.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	private static DBManager instance = null;
	private static final String DB_PASS = "myPassword1234554321";
	private static final String DB_USER = "root";
	public static final String DB_NAME = "IttalentsHomeworks";
	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/" + DB_NAME + "?characterEncoding=utf-8&serverTimezone=UTC";
	private Connection con;

	private DBManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			System.out.println("MMMMMMM");
			System.out.println(con);
		} catch (SQLException | ClassNotFoundException e) {
			System.err.println("Error with connecting to DB: " + e);
		}
	}

	public static synchronized DBManager getInstance() {
		if (instance == null)
			instance = new DBManager();
		return instance;
	}

	public Connection getConnection() {
		return con;
	}

	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			System.err.println("Error with closing DB: " + e);
		}
	}
}