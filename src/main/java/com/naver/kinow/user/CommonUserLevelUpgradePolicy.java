package com.naver.kinow.user;

public class CommonUserLevelUpgradePolicy implements UserLevelUpgradePolicy {

	public static final int COMMON_MIN_LOGCOUNT_FOR_SIVER = 50;
	public static final int COMMON_MIN_RECOMMEND_FRO_GOLD = 30;
	
	@Override
	public boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
		case BASIC:  return (user.getLogin() >= COMMON_MIN_LOGCOUNT_FOR_SIVER);
		case SILVER: return (user.getRecommend() >= COMMON_MIN_RECOMMEND_FRO_GOLD);
		case GOLD:   return false;
		default: throw new IllegalArgumentException("Unknown Level: "+currentLevel);
		}
	}

	@Override
	public int getLevelPointLimit(Level level) {
		switch (level) {
		case SILVER: return COMMON_MIN_LOGCOUNT_FOR_SIVER;
		case GOLD:   return COMMON_MIN_RECOMMEND_FRO_GOLD;
		default: throw new IllegalArgumentException("Unknown Level: "+level);
		}
	}
}
