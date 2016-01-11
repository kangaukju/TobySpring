package spring.learning.jdbcTemplete;

import java.util.List;

import com.naver.kinow.user.User;

public interface UserDao {
	public void add(User user);
	public int getCount();
	public User get(String id);
	public List<User> getAll();
	public void update(User user);
	public void deleteAll();
}
