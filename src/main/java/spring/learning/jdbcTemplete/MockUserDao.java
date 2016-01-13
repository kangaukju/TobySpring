package spring.learning.jdbcTemplete;

import java.util.ArrayList;
import java.util.List;

import com.naver.kinow.user.User;

public class MockUserDao implements UserDao {
	private List<User> users;  // 레벨 업그레이드 후보 user 오브젝트 목록 
	private List<User> updated = new ArrayList<User>(); // 업그레이드 대상 user 오브젝트 목록
	
	public MockUserDao(List<User> users) {
		this.users = users;
	}
	
	public List<User> getUpdated() {
		return updated;
	}
	
	@Override
	public List<User> getAll() {
		return users;
	}

	@Override
	public void update(User user) {
		updated.add(user);
	}


	@Override
	public void add(User user) { throw new UnsupportedOperationException(); }
	@Override
	public int getCount() { throw new UnsupportedOperationException(); }
	@Override
	public User get(String id) { throw new UnsupportedOperationException(); }
	@Override
	public void deleteAll() { throw new UnsupportedOperationException(); }
}
