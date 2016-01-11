package com.naver.kinow.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateUserIdException extends DuplicateKeyException {

	public DuplicateUserIdException(String msg) {
		super(msg);
	}
	
	public DuplicateUserIdException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
