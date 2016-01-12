package com.naver.kinow.user;

public interface UserLevelUpgradePolicy {
	public boolean canUpgradeLevel(User user);
	public int getLevelPointLimit(Level level);
}
