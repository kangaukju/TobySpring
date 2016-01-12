package spring.learning.service;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;

import com.naver.kinow.user.Level;
import com.naver.kinow.user.User;
import com.naver.kinow.user.UserLevelUpgradePolicy;

import spring.learning.jdbcTemplete.UserDao;

public class UserServiceImpl implements UserService {
	private UserDao userDao; // DAO DI
	private UserLevelUpgradePolicy userLevelUpgradePolicy; // Upgrade Policy DI
	private PlatformTransactionManager transactionManager; // Transaction DI
	private MailSender mailSender; // java mail DI
	
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void add(User user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		if (user.getEmail() == null) user.setEmail("kinow@unet.kr");
		userDao.add(user);
	}

	protected void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeUserEmailXML(user);
	}

	/*
	public void upgradeLevels() throws Exception {
		// JdbcTemplate 외부에서 Transaction 사용하는 경우
		TransactionSynchronizationManager.initSynchronization();
		Connection c = DataSourceUtils.doGetConnection(dataSource);
		c.setAutoCommit(false);
		
		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			c.commit();
		} catch (Exception e) {
			c.rollback();
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(c, dataSource);
			TransactionSynchronizationManager.unbindResource(dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}
	}
	*/
	
	/*
	// 스프링이 제공하는 트랜잭션을 이용
	public void upgradeLevels() throws Exception {
		PlatformTransactionManager txManager = null; 
		// DataSourceTransactionManager: JDBC의 로컬 트랜잭션을 이용하는 경우
		txManager = new DataSourceTransactionManager(dataSource);
		
		
		TransactionStatus status = null; 
		// getTransaction: 별도의 트랜잭션 시작없이도 getTransaction를 하면
		//                    트랜잭션 시작을 뜻한다.
		status = txManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			txManager.commit(status);
		} catch (Exception e) {
			txManager.rollback(status);
			throw e;
		}
	}
	*/
	
	public void upgradeLevels() {
		Level prevLevel;
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
				prevLevel = user.getLevel();	
				upgradeLevel(user);
				System.out.println("[upgrade user] "+user.getId() + "level: "+prevLevel.name()+"->"+user.getLevel().name());
			}
		}	
	}
	
	/*
	// 트랜잭션 추상화 이용 & DI 사용
	public void upgradeLevels() throws Exception {
		
		TransactionStatus status = null; 
		// getTransaction: 별도의 트랜잭션 시작없이도 getTransaction를 하면
		//                    트랜잭션 시작을 뜻한다.
		status = transactionManager.getTransaction(new DefaultTransactionDefinition());		
		try {			
			// 비즈니스 로직 시작
			upgradeLevelsInternal();
			// 비즈니스 로직 끝 
			
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}
	}
	*/
	
	private void upgradeLevelsInternal() {
		Level prevLevel;
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
				prevLevel = user.getLevel();	
				upgradeLevel(user);
				System.out.println("[upgrade user] "+user.getId() + "level: "+prevLevel.name()+"->"+user.getLevel().name());
			}
		}		
	}
	
	private void sendUpgradeUserEmailXML(User user) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(user.getEmail());
		msg.setFrom("kinow@unet.kr");
		msg.setSubject("Upgrade 안내");
		msg.setText("사용자님의 등급이 "+user.getLevel().name()+"(으)로 업그레이 되었습니다.");
		mailSender.send(msg);
	}
	
	private void sendUpgradeUserEmailFake(User user) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.user", "kinow@unet.kr");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		
		mailSender.setJavaMailProperties(props);
		mailSender.setPassword("rphyweqzmetlksce");
		
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(user.getEmail());
		msg.setFrom("kinow@unet.kr");
		msg.setSubject("Upgrade 안내");
		msg.setText("사용자님의 등급이 "+user.getLevel().name()+"(으)로 업그레이 되었습니다.");
		mailSender.send(msg);
	}
	
	private void sendUpgradeUserEmailReal(User user) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.user", "kinow@unet.kr");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session s = Session.getInstance(props, null);
		s.setDebug(true);
		
		
		// gmail 2단계 인증 필요
		MimeMessage msg = new MimeMessage(s.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("kinow@unet.kr", "rphyweqzmetlksce");
			}
			
		}));
		
		try {
			msg.setFrom(new InternetAddress("kinow@unet.kr"));
			msg.addRecipient(Message.RecipientType.TO, 
					new InternetAddress("kinow@naver.com"));
			msg.setSubject("Upgrade 안내");
			msg.setText("사용자님의 등급이 "+user.getLevel().name()+
					" (으)로 업그레이 되었습니다.", "UTF-8");
			
			Transport.send(msg);
		} catch (AddressException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
