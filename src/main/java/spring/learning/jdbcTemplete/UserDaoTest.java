package spring.learning.jdbcTemplete;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.naver.kinow.user.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.either;
import static org.junit.matchers.JUnitMatchers.hasItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="applicationContext.xml")
public class UserDaoTest {
	
	@Autowired
	private ApplicationContext context;
	@Autowired
	private UserDao dao;
	@Autowired
	DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		user1 = new User("kinow1", "kangsukju1", "1234");
		user2 = new User("kinow2", "kangsukju2", "5678");
		user3 = new User("kinow3", "kangsukju3", "9012");
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
	}

	@Test
	public void count() throws SQLException {
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	@Test
	public void addAndGet() {
		User user = new User("kaka", "kinow", "qwe123");
		dao.add(user);
		
		User user2 = dao.get(user.getId());
		assertThat(user.getId(), is(user2.getId()));
		assertThat(user.getName(), is(user2.getName()));
		assertThat(user.getPassword(), is(user2.getPassword()));
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() {
		dao.get("unknown_id");
	}
	
	@Test
	public void getAll() {
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
	}
	
	public void checkSameUser(User u1, User u2) {
		assertThat(u1.getId(), is(u2.getId()));
		assertThat(u1.getName(), is(u2.getName()));
		assertThat(u1.getPassword(), is(u2.getPassword()));
	}
}
