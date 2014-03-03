package com.gmail.dengtao.joe.redis4j.exception;

import java.io.IOException;

public class RedisTimeoutException extends IOException {

	private static final long serialVersionUID = 152323303115769453L;

	public RedisTimeoutException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RedisTimeoutException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RedisTimeoutException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RedisTimeoutException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
