package com.gmail.dengtao.joe.redis4j.proto;

import com.gmail.dengtao.joe.redis4j.utils.ByteUtils;
import com.gmail.dengtao.joe.redis4j.utils.StringUtils;

//	For Simple Strings the first byte of the reply is "+"
//	For Errors the first byte of the reply is "-"
//	For Integers the first byte of the reply is ":"
//	For Bulk Strings the first byte of the reply is "$"
//	For Arrays the first byte of the reply is "*"
public class Protocol {

	public static final char SPECIFIER_STRING = '+';
	public static final char SPECIFIER_ERROR = '-';
	public static final char SPECIFIER_INTEGER = ':';
	public static final char SPECIFIER_BULK = '$';
	public static final char SPECIFIER_ARRAY = '*';
	
	public static final String CR  = "\r";
	public static final String LF = "\n";
	public static final String CRLF = "\r\n";
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final byte BTS_SPECIFIER_STRING = (byte) SPECIFIER_STRING;
    public static final byte BTS_SPECIFIER_ERROR = (byte) SPECIFIER_ERROR;
    public static final byte BTS_SPECIFIER_INTEGER = (byte) SPECIFIER_INTEGER;
    public static final byte BTS_SPECIFIER_BULK = (byte) SPECIFIER_BULK;
    public static final byte BTS_SPECIFIER_ARRAY = (byte) SPECIFIER_ARRAY;
    public static final byte[] BTS_CRLF = StringUtils.getBytes(CRLF, DEFAULT_CHARSET);
    
	public enum Command {
		AUTH,	// Request for authentication in a password-protected Redis server.
		SELECT,	// Select the DB with having the specified zero-based numeric index.
		SET,	// Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type.
		GET,	// Get the value of key. If the key does not exist the special value nil is returned.
		APPEND,	// If key already exists and is a string, this command appends the value at the end of the string.
		DEL,	// Removes the specified keys. A key is ignored if it does not exist.
		EXPIRE, // Set a timeout on key. After the timeout has expired, the key will automatically be deleted.
		EXISTS,	// Check if key exists.
		FLUSHDB,// Delete all the keys of the currently selected DB. This command never fails.
		ECHO,	// Returns message.
		PING,	// This command is often used to test if a connection is still alive, or to measure latency.
		QUIT	// Ask the server to close the connection.
		;
	}

	public interface Type {
		public int STRING = 1;
		public int ERROR = 2;
		public int INTEGER = 4;
		public int BULK_STRING = 8;
		public int ARRAY = 16;
	}
	
	/**
	 * Check byte array start with {@link Protocol#SPECIFIER_STRING} | {@link Protocol#SPECIFIER_ERROR} | {@link Protocol#SPECIFIER_INTEGER} | {@link Protocol#SPECIFIER_BULK} | {@link Protocol#SPECIFIER_ARRAY} 
	 * @return
	 */
	public static boolean isValid(byte[] buf) {
		return 
			ByteUtils.startWith(buf, (byte) SPECIFIER_STRING) || 
			ByteUtils.startWith(buf, (byte) SPECIFIER_ERROR) ||
			ByteUtils.startWith(buf, (byte) SPECIFIER_INTEGER) ||
			ByteUtils.startWith(buf, (byte) SPECIFIER_BULK) ||
			ByteUtils.startWith(buf, (byte) SPECIFIER_ARRAY);
	}
}