package spring.learning.jdbcTemplete;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserDao userDao;
	@Autowired
	UserService userService;
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("kinow1", "kaka1", "p1", Level.BASIC, 49, 0),
				new User("kinow2", "kaka2", "p2", Level.BASIC, 50, 0),
				new User("kinow3", "kaka3", "p3", Level.SILVER, 60, 29),
				new User("kinow4", "kaka4", "p4", Level.SILVER, 60, 30),
				new User("kinow5", "kaka5", "p5", Level.GOLD, 100, 100)
				);
		
		userDao.deleteAll();
	}
	
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	@Test
	public void upgradeLevels() {
		for (User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		checkLevel(users.get(0).getId(), Level.BASIC);
		checkLevel(users.get(1).getId(), Level.SILVER);
		checkLevel(users.get(2).getId(), Level.SILVER);
		checkLevel(users.get(3).getId(), Level.GOLD);
		checkLevel(users.get(4).getId(), Level.GOLD);
	}
	
	private void checkLevel(String userId, Level expectedLevel) {
		User user = userDao.get(userId);
		assertThat(user.getLevel(), is(expectedLevel));
	}
	
}
