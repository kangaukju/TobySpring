package spring.learning.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

	private Object object;
	
	public UppercaseHandler(Object object) {
		super();
		this.object = object;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = method.invoke(object, args);
		
		if (ret instanceof String) {
			return ((String) ret).toUpperCase();
		}
		return ret;
	}
	
}
