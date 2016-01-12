package spring.learning.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MockMailServer implements MailSender {
	private List<String> requests = new ArrayList<String>();
	
	public List<String> getRequests() {
		return requests;
	}

	@Override
	public void send(SimpleMailMessage simplemailmessage) throws MailException {
		for (String to : simplemailmessage.getTo()) {
			requests.add(to);
		}
	}

	@Override
	public void send(SimpleMailMessage[] asimplemailmessage) throws MailException {
		for (SimpleMailMessage msg : asimplemailmessage) {
			for (String to : msg.getTo()) {
				requests.add(to);
			}	
		}
	}

}
