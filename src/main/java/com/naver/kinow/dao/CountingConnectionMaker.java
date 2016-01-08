package com.naver.kinow.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker {
	long counter = 0;
	private ConnectionMaker realConnectionMaker;

	public CountingConnectionMaker(ConnectionMaker connectionMaker) {
		this.realConnectionMaker = connectionMaker;
	}
	
	@Override
	public Connection makeConnection() 
			throws ClassNotFoundException, SQLException {		
		this.counter++;
		return this.realConnectionMaker.makeConnection();
	}
	
	public long getCounter() {
		return this.counter;
	}
}
