package com.naver.kinow.user;

public class NewYearUserLevelUpgradePolicy implements UserLevelUpgradePolicy {

	public static final int NEWYEAR_MIN_LOGCOUNT_FOR_SIVER = 50;
	public static final int NEWYEAR_MIN_RECOMMEND_FRO_GOLD = 30;
	
	@Override
	public boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch (currentLevel) {
		case BASIC:  return (user.getLogin() >= NEWYEAR_MIN_LOGCOUNT_FOR_SIVER);
		case SILVER: return (user.getRecommend() >= NEWYEAR_MIN_RECOMMEND_FRO_GOLD);
		case GOLD:   return false;
		default: throw new IllegalArgumentException("Unknown Level: "+currentLevel);
		}
	}
	
	@Override
	public int getLevelPointLimit(Level level) {
		switch (level) {
		case SILVER: return NEWYEAR_MIN_LOGCOUNT_FOR_SIVER;
		case GOLD:   return NEWYEAR_MIN_RECOMMEND_FRO_GOLD;
		default: throw new IllegalArgumentException("Unknown Level: "+level);
		}
	}
}
