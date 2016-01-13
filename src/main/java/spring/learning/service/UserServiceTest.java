package spring.learning.service;

import static com.naver.kinow.user.Level.GOLD;
import static com.naver.kinow.user.Level.SILVER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;
import com.naver.kinow.user.UserLevelUpgradePolicy;

import spring.learning.jdbcTemplete.MockUserDao;
import spring.learning.jdbcTemplete.UserDao;
import spring.learning.proxy.TransactionHandler;
import spring.learning.proxy.UinterfaceImpl;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserDao userDao;
	@Autowired UserService userService;
	@Autowired UserServiceImpl userServiceImpl;
	@Autowired
	UserLevelUpgradePolicy levelUpgradePolicy;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	MailSender mailSender;
	
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("kinow0", "kaka0", "p0", Level.BASIC, levelUpgradePolicy.getLevelPointLimit(SILVER)-1, 0, "kinow@naver.com"),
				new User("kinow1", "kaka1", "p1", Level.BASIC, levelUpgradePolicy.getLevelPointLimit(SILVER), 0, "kinow@naver.com"),
				new User("kinow2", "kaka2", "p2", Level.SILVER, 60, levelUpgradePolicy.getLevelPointLimit(GOLD)-1, "kinow@naver.com"),
				new User("kinow3", "kaka3", "p3", Level.SILVER, 60, levelUpgradePolicy.getLevelPointLimit(GOLD), "kinow@naver.com"),
				new User("kinow4", "kaka4", "p4", Level.GOLD, 100, Integer.MAX_VALUE, "kinow@naver.com")
				);
		
		userDao.deleteAll();
	}
	
	@Test
	public void upgradeAllOrNothingByProxyTransactionHandler() {
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setUserLevelUpgradePolicy(levelUpgradePolicy);
		testUserService.setMailSender(mailSender);
		
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(testUserService);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern("upgradeLevels");
		
		UserService userService = (UserService) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { UserService.class }, 
				txHandler);
		
		for (User user : users) {
			userService.add(user);
		}
		
		try {
			userService.upgradeLevels();
			fail("TestUserServiceException expected...");
		} catch(Exception e) {
		}
		checkLevelUpgrade(users.get(1), false);
	}
	
	@Test
	public void reflectInterfaces() {
		
		Class<?> cls = UinterfaceImpl.class;
		
		Class<?> []classes = cls.getInterfaces();
		Type [] types      = cls.getGenericInterfaces();
		for (Class<?> c : classes) {
			System.out.println("class: "+c.getName());
		}
		for (Type t : types) {
			System.out.println("type: "+t);
		}
	}
	
	@Test
	public void upgradeAllOrNothing() {
		
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setUserLevelUpgradePolicy(levelUpgradePolicy);
		testUserService.setMailSender(mailSender);
		
		UserServiceTx userServiceTx = new UserServiceTx();
		userServiceTx.setTransactionManager(transactionManager);
		userServiceTx.setUserService(testUserService);
		
		for (User user : users) {
			testUserService.add(user);
		}
		
		try {
			userServiceTx.upgradeLevels();
			fail("TestUserServiceException expected...");
		} catch(Exception e) {
		}
		checkLevelUpgrade(users.get(1), false);
	}
	
	
	@Test
	public void add() {
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	@Test
	public void upgradeLevelsByMock() {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MockMailServer mockMailServer = new MockMailServer();
		userServiceImpl.setMailSender(mockMailServer);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		checkUserAndLevel(updated.get(0), "kinow1", Level.SILVER);
		checkUserAndLevel(updated.get(1), "kinow3", Level.GOLD);
		
		List<String> requests = mockMailServer.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(requests.get(0), is(users.get(1).getEmail()));
		assertThat(requests.get(1), is(users.get(3).getEmail()));
	}
	
	@Test
	public void mockUpgradeLevels() {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		// 목 오브젝트가 제공하는 검증 기능을 통해서 어떤 메소드가 몇 번 호출됐는지,
		// 파라미터는 무엇인지 확인할 수 있다.
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		// 파라미터를 정밀하게 검사하기 위해 캡쳐할 수 도 있다.
		ArgumentCaptor<SimpleMailMessage> mailMessageArg =
				ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	@Test
	@DirtiesContext
	public void upgradeLevels() {
		MockMailServer mockMailServer = new MockMailServer();
		userServiceImpl.setMailSender(mockMailServer);
		for (User user : users) {
			userServiceImpl.add(user);
		}
		
		try {
			userServiceImpl.upgradeLevels();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		checkLevel(users.get(0).getId(), Level.BASIC);
		checkLevel(users.get(1).getId(), Level.SILVER);
		checkLevel(users.get(2).getId(), Level.SILVER);
		checkLevel(users.get(3).getId(), Level.GOLD);
		checkLevel(users.get(4).getId(), Level.GOLD);
		*/
		checkLevelUpgrade(users.get(0), false);
		checkLevelUpgrade(users.get(1), true);
		checkLevelUpgrade(users.get(2), false);
		checkLevelUpgrade(users.get(3), true);
		checkLevelUpgrade(users.get(4), false);
		
		List<String> requests = mockMailServer.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(requests.get(0), is(users.get(1).getEmail()));
		assertThat(requests.get(1), is(users.get(3).getEmail()));
	}
	
	private void checkLevel(String userId, Level expectedLevel) {
		User user = userDao.get(userId);
		assertThat(user.getLevel(), is(expectedLevel));
	}
	
	private void checkLevelUpgrade(User user, boolean upgrade) {
		User userUpdate = userDao.get(user.getId());
		if (upgrade) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
}
