package com.naver.kinow.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LocalConnectionMaker implements ConnectionMaker {

	@Override
	public Connection makeConnection() 
			throws ClassNotFoundException, SQLException {
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection(
				"jdbc:mysql://localhost/spring", "kinow", "830421");
		return c;
	}

}
