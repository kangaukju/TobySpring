package spring.learning.jdbcTemplete;

public class Test {
	
	class Exception1 extends Exception {
		public Exception1() { super(); }
		public Exception1(Throwable cause) {
			super(cause);
		}
	}
	
	class Exception2 extends Exception1 {
		public Exception2() { super(); }
		public Exception2(Throwable cause) {
			super(cause);
		}
	}
		
	class Exception3 extends Exception2 {
		public Exception3() { super(); }
		public Exception3(Throwable cause) {
			super(cause);
		}
	}

	
	public void raiseException1() throws Exception1 {
		try {
			raiseException2();
		} catch (Exception2 e) {
			throw new Exception1(e);
		}
	}
	
	public void raiseException2() throws Exception2 {
		try {
			raiseException3();
		} catch (Exception3 e) {
			throw new Exception2(e);
		}
	}
	
	public void raiseException3() throws Exception3 {
		throw new Exception3();
	}
	
	public void printThrowable(Exception e) {
		Throwable cause = e.getCause();
		while (cause != null) {
			System.out.println("[printThrowable]==> "+cause);
			cause = cause.getCause();
		}
	}
	public void printRootCause(Exception e) {
		Throwable rootCause = null;
		Throwable cause = e.getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		System.out.println("[printRootCause]==> "+rootCause);
	}
	
	public static void main(String[] args) {
		
		Test t = new Test();
		try {
			t.raiseException1();
		} catch (Exception1 e) {
			Throwable cause = e.getCause();
			t.printThrowable(e);
			t.printRootCause(e);
		}
	}

}
