package spring.learning.proxy;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

public class SecurityHandlerFactoryBean implements FactoryBean<Object> {
	// 부가기능을 제공할 타깃 오브젝트. 어떤 타입의 오브젝트에도 적용 가능하다.
	private Object target;
	// 트랜잭션을 적용할 메서드 이름 패턴
	private String pattern;
	
	// 다이나믹 프록시를 생성할 때 필요하다. 
	// UserService 외의 (어떠한)인터페이스를 가진 target에도 적용 가능하다.
	private Class<?> serviceInterface;
	
	
	public void setTarget(Object target) {
		this.target = target;
	}	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	
	@Override
	public Object getObject() throws Exception {
		SecurityHandler scHandler = new SecurityHandler();
		scHandler.setTarget(target);
		scHandler.setPattern(pattern);
		return Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { serviceInterface },
				scHandler);
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
