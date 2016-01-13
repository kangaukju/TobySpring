package spring.learning.proxy.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/learning/proxy/factory/FactoryBeanApplicationContext.xml")
public class TestFactoryBean {
	@Autowired
	private Message message;
	@Autowired
	ApplicationContext context;
	
	@Test
	public void checkFactoryBean() {
		System.out.println("message object is "+message);
		System.out.println("message's text is "+message.getText());
		
		Object messageBean = context.getBean("message");
		assertThat(messageBean, is(Message.class));
		assertThat(((Message)messageBean).getText(), is(message.getText()));
	}
	
	@Test
	public void getFactoryBean() {
		Object factory = context.getBean("&message");
		assertThat(factory, is(MessageFactoryBean.class));
	}
}
