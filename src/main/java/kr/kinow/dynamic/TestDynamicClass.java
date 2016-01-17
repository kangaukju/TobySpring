package kr.kinow.dynamic;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestDynamicClass {

    private static final Class constructorParams[] = {InvocationHandler.class};
	
	public static void main(String[] args) throws Exception {
		
		Class clazz = Class.forName("kr.kinow.dynamic.TestClass");
		
		Constructor cons = clazz.getConstructor(constructorParams);
		
		TestClass ts = (TestClass) cons.newInstance();
		
		for (Method m : ts.getClass().getMethods()) {
			if (!Modifier.isFinal(m.getModifiers())) {
				System.out.println(m.getName());
			}
		}
	}

}
