package spring.learning.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;

public class TestProxy {
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("kinow"), is("Hello kinow"));
		assertThat(hello.sayHi("kinow"), is("Hi kinow"));
		assertThat(hello.sayThankYou("kinow"), is("ThankYou kinow"));
	}
	
	@Test
	public void simpleProxy2() {
		Hello proxiedHello = new HelloUppercase(new HelloTarget());
		assertThat(proxiedHello.sayHello("kinow"), is("HELLO KINOW"));
		assertThat(proxiedHello.sayHi("kinow"), is("HI KINOW"));
		assertThat(proxiedHello.sayThankYou("kinow"), is("THANKYOU KINOW"));
	}
	
	@Test
	public void simpleProxy3() {
		// 생성된 다이나믹 프록시 오브젝트는 Hello 인터페이스를 구현하고 있으므로 Hello 타입으로 캐스팅해도 안전하다.
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
				getClass().getClassLoader(), // 동적으로 생성되는 다이나믹 프로시 클래스의 로딩에 사용할 클래스 로더
				new Class[] { Hello.class }, // 구현할 인터페이스
				new HelloUppercaseHandler(new HelloTarget())); // 부가기능과 위임 코드를 담은 InvocationHandler
		
		assertThat(proxiedHello.sayHello("kinow"), is("HELLO KINOW"));
		assertThat(proxiedHello.sayHi("kinow"), is("HI KINOW"));
		assertThat(proxiedHello.sayThankYou("kinow"), is("THANKYOU KINOW"));
	}
	
	@Test
	public void simpleProxy4() {
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { Hello.class },
				new UppercaseHandler(new HelloTarget()));
		
		assertThat(proxiedHello.sayHello("kinow"), is("HELLO KINOW"));
		assertThat(proxiedHello.sayHi("kinow"), is("HI KINOW"));
		assertThat(proxiedHello.sayThankYou("kinow"), is("THANKYOU KINOW"));
	}
	
	@Test
	public void simpleProxy5() {
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { Hello.class },
				new UppercaseHandler2(new HelloTarget()));
		
		assertThat(proxiedHello.sayHello("kinow"), is("HELLO KINOW"));
		assertThat(proxiedHello.sayHi("kinow"), is("HI KINOW"));
		assertThat(proxiedHello.sayThankYou("kinow"), is("THANKYOU KINOW"));
	}
}
