package spring.learning.service;

import static com.naver.kinow.user.Level.GOLD;
import static com.naver.kinow.user.Level.SILVER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;
import com.naver.kinow.user.UserLevelUpgradePolicy;

import spring.learning.jdbcTemplete.UserDao;
import spring.learning.proxy.Hello;
import spring.learning.proxy.HelloTarget;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="TX_ApplicationContext.xml")
public class UserServiceFactoryBeanTest {
	@Autowired UserLevelUpgradePolicy newYearUserLevelUpgradePolicy;
	@Autowired ApplicationContext context;
	@Autowired UserService userService;
	@Autowired UserDao userDao;
	@Autowired PlatformTransactionManager transactionManager;
	@Autowired UserService testUserServiceImpl;
	@Autowired UserService userServiceFactoryBean;
	
	List<User> users;
	
	Log log = LogFactory.getLog(getClass());
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("kinow0", "kaka0", "p0", Level.BASIC, newYearUserLevelUpgradePolicy.getLevelPointLimit(SILVER)-1, 0, "kinow@naver.com"),
				new User("kinow1", "kaka1", "p1", Level.BASIC, newYearUserLevelUpgradePolicy.getLevelPointLimit(SILVER), 0, "kinow@naver.com"),
				new User("kinow2", "kaka2", "p2", Level.SILVER, 60, newYearUserLevelUpgradePolicy.getLevelPointLimit(GOLD)-1, "kinow@naver.com"),
				new User("kinow3", "kaka3", "p3", Level.SILVER, 60, newYearUserLevelUpgradePolicy.getLevelPointLimit(GOLD), "kinow@naver.com"),
				new User("kinow4", "kaka4", "p4", Level.GOLD, 100, Integer.MAX_VALUE, "kinow@naver.com")
				);
		userDao.deleteAll();
	}
		
	@Test
	public void classNamePointcutAdvisor() {
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter() {
//				System.out.println("who?? call me??");
				return new ClassFilter() {
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};
		classMethodPointcut.setMappedName("sayH*");
		
		checkAdviced(new HelloTarget(), classMethodPointcut, true);
		
		class HelloWorld extends HelloTarget {};
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		class HelloToto extends HelloTarget {};
		checkAdviced(new HelloToto(), classMethodPointcut, true);
	}
	private void checkAdviced(Object target, Pointcut pointcut, boolean advied) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if (advied) {
			assertThat(proxiedHello.sayHello("kinow"), is("HELLO KINOW"));
			assertThat(proxiedHello.sayHi("kinow"), is("HI KINOW"));
			assertThat(proxiedHello.sayThankYou("kinow"), is("ThankYou kinow"));	
		} else {
			assertThat(proxiedHello.sayHello("kinow"), is("Hello kinow"));
			assertThat(proxiedHello.sayHi("kinow"), is("Hi kinow"));
			assertThat(proxiedHello.sayThankYou("kinow"), is("ThankYou kinow"));
		}
	}
	
	@Test
	public void pointcutAdvisor() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		log.info(proxiedHello.sayHello("kinow"));
		log.info(proxiedHello.sayHi("kinow"));
		log.info(proxiedHello.sayThankYou("kinow"));	
		
	}
	
	//////////////////////////////////////////////////////////
	// reflect Porxy Factory Bean TEST
	//////////////////////////////////////////////////////////
	@Test
	public void upgradeAllOrNothing() {
		for (User user : users) {
			userService.add(user);
		}
		
		try {
			userService.upgradeLevels();
			fail("TestUserServiceException expected...");
		} catch(Exception e) {
		}
		
		checkLevelUpgrade(users.get(1), false);
	}
	@Test
	public void upgradeAllOrNothing2() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		
		pfBean.setTarget(testUserServiceImpl);
		log.info("complete setTarget");		
		pfBean.addAdvice(new SecurityAdvice());
		log.info("complete addAdvice SecurityAdvice");
		pfBean.addAdvice(new TransactionAdvice());
		log.info("complete addAdvice TransactionAdvice");
		
		userService = (UserService) pfBean.getObject();
		
		for (User user : users) {
			userService.add(user);
		}
		
		try {
			userService.upgradeLevels();
			fail("TestUserServiceException expected...");
		} catch(Exception e) {
		}
		
		checkLevelUpgrade(users.get(1), false);
	}
	class TransactionAdvice implements MethodInterceptor {		
		
		@Override
		public Object invoke(MethodInvocation methodinvocation) throws Throwable {
			log.debug("!!! [start TransactionAdvice] !!!");
			TransactionStatus status = transactionManager
					.getTransaction(new DefaultTransactionDefinition());
			try {
				Object ret = methodinvocation.proceed();
				transactionManager.commit(status);
				log.debug("!!! [end TransactionAdvice] !!!");
				return ret;
			} catch (RuntimeException e) {
				transactionManager.rollback(status);
				log.debug("!!! [end TransactionAdvice - exception] !!!");
				throw e;
			}
		}
		
	}
	class SecurityAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation methodinvocation) throws Throwable {
			log.debug("!!! [start SecurityAdvice] !!!");
			try {
				Object ret = methodinvocation.proceed();
				log.debug("!!! [end SecurityAdvice] !!!");
				return ret;
			} catch (RuntimeException e) {
				log.debug("!!! [end SecurityAdvice - exception] !!!");
				throw e;
			}
		}
		
	}
	
	
	
	//////////////////////////////////////////////////////////
	// Spring Porxy Factory Bean TEST
	//////////////////////////////////////////////////////////
	@Test
	public void proxyFactoryBean() {
		
		
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("kinow"), is("HELLO KINOW"));
		assertThat(proxiedHello.sayHi("kinow"), is("HI KINOW"));
		assertThat(proxiedHello.sayThankYou("kinow"), is("THANKYOU KINOW"));
		
	}
	class UppercaseAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();
			return ret.toUpperCase();
		}
	}
	
	//////////////////////////////////////////////////////////
	// Spring Porxy Factory Bean TEST
	//////////////////////////////////////////////////////////
	@Test
	public void proxyFactoryBean2() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());
		pfBean.addAdvice(new ReverseAdvice());
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		log.debug(proxiedHello.sayHello("kinow"));
		log.debug(proxiedHello.sayHi("kinow"));
		log.debug(proxiedHello.sayThankYou("kinow"));
		
	}
	class ReverseAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();
			StringBuffer sb = new StringBuffer();
			char [] retArr = ret.toCharArray();
			for (int i=ret.length()-1; i>=0; i--) {
				sb.append(retArr[i]);
			}
			return sb.toString();
		}		
	}
	
	@Test
	public void upgradeAllOrNothingProxyFactoryBean() {
		for (User user : users) {
			userServiceFactoryBean.add(user);
		}
		
		try {
			userServiceFactoryBean.upgradeLevels();
			fail("TestUserServiceException expected...");
		} catch(Exception e) {
		}
		
		checkLevelUpgrade(users.get(1), false);
	}
	
	
	private void checkLevelUpgrade(User user, boolean upgrade) {
		User userUpdate = userDao.get(user.getId());
		if (upgrade) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
}
