package spring.learning.service;

import static com.naver.kinow.user.Level.GOLD;
import static com.naver.kinow.user.Level.SILVER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;
import com.naver.kinow.user.UserLevelUpgradePolicy;

import spring.learning.jdbcTemplete.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserDao userDao;
	@Autowired
	UserServiceImpl userService;
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
		
		userService.setMailSender(mailSender);
	}
	
	@Test
	public void upgradeAllOrNothing() {
		UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setUserLevelUpgradePolicy(levelUpgradePolicy);
		testUserService.setTransactionManager(transactionManager);
		testUserService.setMailSender(mailSender);
		
		for (User user : users) {
			testUserService.add(user);
		}
		
		try {
			testUserService.upgradeLevels();
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
	@DirtiesContext
	public void upgradeLevels() {
		for (User user : users) {
			userService.add(user);
		}
		
		MockMailServer mockMailServer = new MockMailServer();
		userService.setMailSender(mockMailServer);
		
		try {
			userService.upgradeLevels();
		} catch (Exception e) {
			
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
		for (String mail: requests) {
			System.out.println("[send email] "+mail);
		}
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
