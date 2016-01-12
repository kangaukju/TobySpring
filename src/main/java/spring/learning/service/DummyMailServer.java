package spring.learning.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailServer implements MailSender {

	@Override
	public void send(SimpleMailMessage simplemailmessage) throws MailException {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(SimpleMailMessage[] asimplemailmessage) throws MailException {
		// TODO Auto-generated method stub

	}

}
