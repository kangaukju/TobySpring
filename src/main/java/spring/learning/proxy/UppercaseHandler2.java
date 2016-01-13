package spring.learning.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler2 implements InvocationHandler {
	
	private Object object;
	
	public UppercaseHandler2(Object object) {
		super();
		this.object = object;
	}
	
	@Override
	public Object invoke(Object obj, Method method, Object[] aobj) throws Throwable {
		Object ret = method.invoke(object, aobj);
		
		if (ret instanceof String && method.getName().startsWith("say")) {
			return ((String) ret).toUpperCase();
		}
		return ret;
	}

}
