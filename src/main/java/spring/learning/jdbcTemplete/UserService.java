package spring.learning.jdbcTemplete;

import java.util.List;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;

public class UserService {
	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			boolean changed = false;
			
			switch (user.getLevel()) {
			case BASIC:
				if (user.getLogin() >= 50) {
					user.setLevel(Level.SILVER);
					changed = true;
				}
				break;
			case SILVER:
				if (user.getRecommend() >= 30) {
					user.setLevel(Level.GOLD);
					changed = true;
				}
				break;
			case GOLD:
				changed = false;
				break;
			}
			
			if (changed) {
				userDao.update(user);
			}
		}
	}
}
