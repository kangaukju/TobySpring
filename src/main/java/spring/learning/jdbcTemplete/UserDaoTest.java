package spring.learning.jdbcTemplete;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="applicationContext.xml")
public class UserDaoTest {
	
	@Autowired
	private ApplicationContext context;
	@Autowired
	private UserDaoJdbc dao;
	@Autowired
	DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		user1 = new User("kinow1", "kangsukju1", "1234", Level.BASIC, 1, 0, null);
		user2 = new User("kinow2", "kangsukju2", "5678", Level.SILVER, 55, 10, null);
		user3 = new User("kinow3", "kangsukju3", "9012", Level.GOLD, 100, 40, null);
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
	}
	
	@Test
	public void update() {		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("kinowUpdate");
		user1.setPassword("qwe123qwe123");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	@Test
	public void sqlExceptionTranslate() {
		try {
			dao.add(user1);
			dao.add(user1);
		}
		catch (DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException) ex.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(dataSource);
			assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
			
			Throwable cause = ex.getCause();
			for (; cause != null; cause = cause.getCause()) {
				System.out.println("[Throwable] : "+ cause);
			}
			System.out.println("[ex.getRootCause()] : "+ex.getRootCause());
			System.out.println("[ex.getRootCause()] : "+sqlEx);
			System.out.println("[translate] : "+set.translate(null, null, sqlEx));
		}
	}
	
	@Test(expected=DuplicateKeyException.class)
	public void duplicateKey() {
		dao.add(user1);
		dao.add(user1);
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
		User user = new User("kaka", "kinow", "qwe123", Level.BASIC, 1, 0, null);
		dao.add(user);
		
		User user2 = dao.get(user.getId());
		checkSameUser(user, user2);
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() {
		dao.get("unknown_id");
	}
	
	@Test
	public void getAll() {		
		// getAll 시 아무것도 없는 경우에 대한 테스트 
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
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
		assertThat(u1.getLevel(), is(u2.getLevel()));
		assertThat(u1.getLogin(), is(u2.getLogin()));
		assertThat(u1.getRecommend(), is(u2.getRecommend()));
	}
}
