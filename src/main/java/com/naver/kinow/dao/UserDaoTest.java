package com.naver.kinow.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.naver.kinow.user.User;

public class UserDaoTest {

	public static void testDao(UserDao dao) throws ClassNotFoundException, SQLException {		
		User user = new User();
		user.setId("kinow");
		user.setName("강석주");
		user.setPassword("qwe123");
		
		dao.add(user);
		
		System.out.println("Add User: "+user);
		
		user = dao.get(user.getId());
		System.out.println("Get User: "+user);
	} 
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		/*
		ApplicationContext context = 
				new AnnotationConfigApplicationContext(DaoFactory.class);
		
		UserDao dao = context.getBean("userDao", UserDao.class);		
		testDao(dao);
		
		ConnectionMaker connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
		UserDao dao = new UserDao(connectionMaker);
		test(dao);
		*/
		
		// testCountingDaoFactory();
		
		// testXMLSpringContainer();
		
		testXMLSpringContainer2();
		
		// testDaoFactory();
		
	}
	
	public static void testXMLSpringContainer2() throws ClassNotFoundException, SQLException {
		// UserDao.class 클래스 path 위치에서 "applicationContext.xml" 설정파일을 찾는다.
		ApplicationContext context 
			= new ClassPathXmlApplicationContext("applicationContext.xml", UserDao.class);
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		testDao(dao);
	}
	
	public static void testXMLSpringContainer() throws ClassNotFoundException, SQLException {
		
		String xmlfile = "com/naver/kinow/dao/applicationContext.xml";
		
		ApplicationContext context 
			= new GenericXmlApplicationContext(xmlfile);
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		testDao(dao);
	}
	
	public static void testCountingDaoFactory() throws ClassNotFoundException, SQLException {
		ApplicationContext context 
			= new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		UserDao dao = context.getBean("userDao", UserDao.class);
		testDao(dao);
		
		CountingConnectionMaker ccm 
			= context.getBean("connectionMaker", CountingConnectionMaker.class);
		System.out.println(ccm.getCounter());
		
		dao.get("kinow");
		dao.get("kinow");
		dao.get("kinow");
		System.out.println(ccm.getCounter());
	}

	public static void testDaoFactory() throws ClassNotFoundException, SQLException {
		ApplicationContext context 
			= new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = context.getBean("userDao", UserDao.class);
		testDao(dao);		
	}
}

