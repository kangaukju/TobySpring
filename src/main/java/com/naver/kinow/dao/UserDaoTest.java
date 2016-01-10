package com.naver.kinow.dao;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.naver.kinow.user.User;



// 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@RunWith(SpringJUnit4ClassRunner.class)
// 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
// 수백 개의 테스트 클래스를 만들었어도 같은 "설정파일"의 
// 애플리케이션 컨텍스트를 하나만 만들고 여러 클래스가 공유하는 효율적인 방
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
@ContextConfiguration(locations="applicationContext.xml")
// @ContextConfiguration(locations="test-applicationContext.xml")
// 테스트 메소드에서 애플리케이션 컨텍스트의 구성이나 상태를 
// 변경한다는 것을 테스트 컨텍스트 프레임워크에 알려주면 @Test마다 새로운
// 애플리케이션 컨텍스트를 생성할 것이다???
@DirtiesContext
public class UserDaoTest {
	// 테스트 오브젝트가 만들어지고 나면 스프링 테스트 컨텍스트에 의해 자동으로 값이 주입된다.
	// @Autowired: 변수 타입과 일치하는 컨텍스트 내의 빈을 찾는다.
	@Autowired
	private ApplicationContext context;
	@Autowired
	private UserDao dao;
	private User user1;
	private User user2;
	private User user3;
	@Autowired
	SimpleDriverDataSource simpleDriverDataSource;
	@Autowired
	DataSource dataSource;
	
	@Before
	public void setUp() {
		// @Autowired 사용으로 변경
		// dao = context.getBean("userDao", UserDao.class);
		
		user1 = new User("kinow1", "강석주1", "1234");
		user2 = new User("kinow2", "강석주2", "5678");
		user3 = new User("kinow3", "강석주3", "9012");
		
		/*
		// DI 관계를 여기서 바꾸는 바람직 못한 행동이다.
		DataSource testDataSource = new SingleConnectionDataSource(
				"jdbc:mysql://localhost/spring", "kinow", "830421", true);
		dao.setDataSource(testDataSource);
		*/
		
		
		System.out.println("JUnit before==================");
		System.out.println("context: "+context);
//		System.out.println("this: "+this);
//		System.out.println("dataSource: "+dataSource);
//		System.out.println("simpleDriverDataSource: "+simpleDriverDataSource);
//		System.out.println(dataSource == simpleDriverDataSource);
		System.out.println("==============================");
	}
	
	@Test
	public void testAutowiredAndGetBean() {
		DataSource autowiredDataSource = this.dataSource;
		DataSource getBeanDataSource = context.getBean("dataSource", DataSource.class);
		
		Assert.assertThat(autowiredDataSource, Is.is(getBeanDataSource));
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		dao.deleteAll();
		Assert.assertThat(dao.getCount(), Is.is(0));
		
		dao.get("unknown_id");
	}
	
	@Test
	public void count() throws SQLException {
		dao.deleteAll();
		Assert.assertThat(dao.getCount(), Is.is(0));
		
		dao.add(user1);
		Assert.assertThat(dao.getCount(), Is.is(1));
		
		dao.add(user2);
		Assert.assertThat(dao.getCount(), Is.is(2));
		
		dao.add(user3);
		Assert.assertThat(dao.getCount(), Is.is(3));
	}
	
	@Test
	public void addAndGet() throws SQLException {
		String xmlfile = "com/naver/kinow/dao/applicationContext.xml";
		ApplicationContext context 
			= new GenericXmlApplicationContext(xmlfile);
		UserDao dao = context.getBean("userDao", UserDao.class);
		User user = new User();
		user.setId("kaka");
		user.setName("강석주");
		user.setPassword("qwe123");
		
		dao.deleteAll();
		Assert.assertThat(dao.getCount(), Is.is(0));
		
		dao.add(user);
		Assert.assertThat(dao.getCount(), Is.is(1));
		
		User user2 = dao.get(user.getId());
		
		Assert.assertThat(user.getId(), Is.is(user2.getId()));
		//Assert.assertThat(user.getName(), Is.is(user2.getName()));
		Assert.assertThat(user.getPassword(), Is.is(user2.getPassword()));
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
		
		testCountingDaoFactory();
		// testXMLSpringContainer();
		// testXMLSpringContainer2();
		// testDaoFactory();
		
		/*
		UserDaoTest test = new UserDaoTest();
		test.addAndGet();
		*/
		
		// for JUnit test
		// JUnitCore.main("com.naver.kinow.dao.UserDaoTest");
	}
}

