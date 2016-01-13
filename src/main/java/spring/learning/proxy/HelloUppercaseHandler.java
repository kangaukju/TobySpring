package spring.learning.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HelloUppercaseHandler implements InvocationHandler {
	
	Hello target;
	
	public HelloUppercaseHandler(Hello target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String ret = (String) method.invoke(target, args);
		return ret.toUpperCase();
	}

}
