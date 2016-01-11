package com.naver.kinow.user;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserTest {
	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}
	
	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for (Level lv : levels) {
			if (lv.nextLevel() == null) continue;
			user.setLevel(lv);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(lv.nextLevel()));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void canNotUpgradeLevel() {
		Level[] levels = Level.values();
		for (Level lv : levels) {
			if (lv.nextLevel() != null) continue;
			user.setLevel(lv);
			user.upgradeLevel();
		}
	}
}
