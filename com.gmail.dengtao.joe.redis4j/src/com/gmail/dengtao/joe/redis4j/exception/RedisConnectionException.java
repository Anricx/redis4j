package com.gmail.dengtao.joe.redis4j.exception;

import java.net.ConnectException;

public class RedisConnectionException extends ConnectException {

	private static final long serialVersionUID = -4297825755969026496L;

	public RedisConnectionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RedisConnectionException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}