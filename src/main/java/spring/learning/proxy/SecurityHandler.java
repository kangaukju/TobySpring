package spring.learning.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class SecurityHandler implements InvocationHandler {
	// 부가기능을 제공할 타깃 오브젝트. 어떤 타입의 오브젝트에도 적용 가능하다.
	private Object target;
	// 트랜잭션을 적용할 메서드 이름 패턴
	private String pattern;

	public void setTarget(Object target) {
		this.target = target;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().startsWith(pattern)) {
			return invokeInSecurity(method, args);
		}
		return method.invoke(target, args); 
	}

	public Object invokeInSecurity(Method method, Object[] args) throws Throwable {
		System.out.println("!!! [Start Security] !!!");
		try {
			Object ret = method.invoke(target, args);
			System.out.println("!!! [End Security - ok] !!!");			
			return ret;
		} catch (InvocationTargetException e) {
			System.out.println("!!! [End Security - occur exception] !!!");
			throw e.getTargetException();
		}
	}
}
