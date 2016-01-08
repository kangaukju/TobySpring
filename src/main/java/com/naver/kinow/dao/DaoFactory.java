package com.naver.kinow.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {

	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost/spring");
		dataSource.setUsername("kinow");
		dataSource.setPassword("830421");
		
		System.out.println("Using data source");
		
		return dataSource;
	}

	@Bean
	public UserDao userDao() {
		UserDao userDao = new UserDao();
		// userDao.setConnectionMaker(connectionMaker());
		userDao.setDataSource(dataSource());
		return userDao;
	}

	@Bean
	public ConnectionMaker connectionMaker() {
		// NaverConnectionMaker => 높은 결합도
		return new NaverConnectionMaker();
	}
}
