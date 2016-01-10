package spring.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	
	public String concatenate(String file) throws IOException {
		LineCallback<String> callback = new LineCallback<String>() {
			@Override
			public String doSomethingWithLine(String line, String value) {
				return value + line;
			}
		};
		return lineReadTemplate(file, callback, "");
	}
	
	public Integer calcMultiply(String file) throws IOException {
		/*
		BufferedReaderCallback sumCallack = 
				new BufferedReaderCallback() {
					@Override
					public Integer doSomethingWithReader(BufferedReader br) 
							throws IOException {
						Integer sum = 1;
						String line = null;
						while ((line = br.readLine()) != null) {
							sum *= Integer.valueOf(line);
						}
						return sum;
					}
				};
		return fileReadTemplate(file, sumCallack);
		*/
		
		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return Integer.valueOf(line) * value;
			}
		};
		return lineReadTemplate(file, sumCallback, 1);
	}
	
	public Integer calcSum(String file) throws IOException {
		/*
		BufferedReaderCallback sumCallack = 
				new BufferedReaderCallback() {
					@Override
					public Integer doSomethingWithReader(BufferedReader br) 
							throws IOException {
						Integer sum = 0;
						String line = null;
						while ((line = br.readLine()) != null) {
							sum += Integer.valueOf(line);
						}
						return sum;
					}
				};
		return fileReadTemplate(file, sumCallack);
		*/
		
		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {
			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return Integer.valueOf(line) + value;
			}
		};
		return lineReadTemplate(file, sumCallback, 0);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// LineCallback 구현 
	////////////////////////////////////////////////////////////////////////////
	public <T> T lineReadTemplate(
			String file, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			T ret = initVal;
			String line = null;
			while ((line = br.readLine()) != null) {
				// ret은 연산을 계속 할 수 있도록 저장해 둔다.
				ret = callback.doSomethingWithLine(line, ret);
			}
			return ret;
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		finally {
			if (br != null)
				try { br.close(); } 
				catch (IOException e) { System.out.println(e.getMessage()); }
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	// BufferedReaderCallback 구현
	////////////////////////////////////////////////////////////////////////////	
	private Integer fileReadTemplate(String file, BufferedReaderCallback callback) 
			throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			return callback.doSomethingWithReader(br);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		finally {
			if (br != null)
				try { br.close(); } 
				catch (IOException e) { System.out.println(e.getMessage()); }
		}
	}
}
