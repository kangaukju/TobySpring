package spring.learning.proxy.factory;

public class Message {
	String text;
	
	public String getText() {
		return text;
	}
	
	public Message(String text) {
		super();
		this.text = text;
	}
	
	public static Message getMessage(String text) {
		return new Message(text);
	}
}
