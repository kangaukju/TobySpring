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
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;
import com.naver.kinow.user.UserLevelUpgradePolicy;

import spring.learning.jdbcTemplete.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="TX_ApplicationContext.xml")
public class UserServiceFactoryBeanTest {
	@Autowired UserLevelUpgradePolicy newYearUserLevelUpgradePolicy;
	@Autowired ApplicationContext context;
	@Autowired UserService userService;
	@Autowired UserDao userDao;
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("kinow0", "kaka0", "p0", Level.BASIC, newYearUserLevelUpgradePolicy.getLevelPointLimit(SILVER)-1, 0, "kinow@naver.com"),
				new User("kinow1", "kaka1", "p1", Level.BASIC, newYearUserLevelUpgradePolicy.getLevelPointLimit(SILVER), 0, "kinow@naver.com"),
				new User("kinow2", "kaka2", "p2", Level.SILVER, 60, newYearUserLevelUpgradePolicy.getLevelPointLimit(GOLD)-1, "kinow@naver.com"),
				new User("kinow3", "kaka3", "p3", Level.SILVER, 60, newYearUserLevelUpgradePolicy.getLevelPointLimit(GOLD), "kinow@naver.com"),
				new User("kinow4", "kaka4", "p4", Level.GOLD, 100, Integer.MAX_VALUE, "kinow@naver.com")
				);
		userDao.deleteAll();
	}
	

	@Test
	public void upgradeAllOrNothing() {
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
	
	
	
	
	private void checkLevelUpgrade(User user, boolean upgrade) {
		User userUpdate = userDao.get(user.getId());
		if (upgrade) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
}
