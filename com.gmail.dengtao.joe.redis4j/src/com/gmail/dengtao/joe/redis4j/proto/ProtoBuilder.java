package com.gmail.dengtao.joe.redis4j.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gmail.dengtao.joe.redis4j.utils.StringUtils;

/**
 * Redis protocol builder. designed for serialize args to RESP.
 * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
 * @version 1.0
 */
public class ProtoBuilder {
	
	private int type = 0;	// protocol data type
	private String charset = Protocol.DEFAULT_CHARSET; // charset
	private List<Object> args = new ArrayList<Object>(); // args

	/**
	 * Get charset for current builder.
	 * @return
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Set charset for this builder.
	 * @param charset charset name
	 */
	public ProtoBuilder setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	/**
	 * <p>
	 * Simple Strings are encoded in the following way: 
	 * a plus character, followed by a string that cannot contain a CR or LF character (no newlines are allowed), 
	 * terminated by CRLF (that is "\r\n").
	 * Simple Strings are used to transmit non binary safe strings with minimal overhead.
	 * </p>
	 * @param value
	 * @return this
	 */
	public ProtoBuilder string(String value) {
		if (value == null) throw new IllegalArgumentException("null value detected!");
		if (value.contains(Protocol.CR) || value.contains(Protocol.LF)) {
			new IllegalArgumentException("CR or LF was not allowed!");
		}
		type = Protocol.Type.STRING;
		args.clear();
		args.add(value);
		return this;
	}
	
	/**
	 * <p>
	 * RESP has a specific data type for errors. 
	 * Actually errors are exactly like RESP Simple Strings, 
	 * but the first character is a minus '-' character instead of a plus. 
	 * The real difference between Simple Strings and 
	 * Errors in RESP is that errors are treated by clients as exceptions, 
	 * and the string that composes the Error type is the error message itself.
	 * </p>
	 * @param msg
	 * @return this
	 */
	public ProtoBuilder error(String msg) {
		type = Protocol.Type.ERROR;
		args.clear();
		args.add(msg);
		return this;
	}
	
	/**
	 * <p>
	 * This type of is just a CRLF terminated string representing an integer.
	 * </p>
	 * @param value
	 * @return this
	 */
	public ProtoBuilder integer(int value) {
		type = Protocol.Type.INTEGER;
		args.clear();
		args.add(value);
		return this;
	}
	
	/**
	 * <p>
	 * Bulk Strings are used in order to represent a single binary safe string up to 512 MB in length.
	 * </p>
	 * @param value
	 * @return this
	 */
	public ProtoBuilder bulk(String value) {
		type = Protocol.Type.BULK_STRING;
		args.clear();
		args.add(value);
		return this;
	}
	
	/**
	 * <p>
	 * int Arrays
	 * </p>
	 * @param args
	 * @return this
	 */
	public ProtoBuilder array(int[] args) {
		type = Protocol.Type.ARRAY;
		this.args.clear();
		for (Object arg : args) {
			this.args.add(arg);
		}
		return this;
	}
	
	/**
	 * <p>
	 * Integer Arrays
	 * </p>
	 * @param args
	 * @return this
	 */
	public ProtoBuilder array(Integer... args) {
		type = Protocol.Type.ARRAY;
		this.args.clear();
		for (Object arg : args) {
			this.args.add(arg);
		}
		return this;
	}
	
	/**
	 * <p>
	 * long Arrays
	 * </p>
	 * @param args
	 * @return this
	 */
	public ProtoBuilder array(long[] args) {
		type = Protocol.Type.ARRAY;
		this.args.clear();
		for (Object arg : args) {
			this.args.add(arg);
		}
		return this;
	}
	
	/**
	 * <p>
	 * Long Arrays
	 * </p>
	 * @param args
	 * @return this
	 */
	public ProtoBuilder array(Long... args) {
		type = Protocol.Type.ARRAY;
		this.args.clear();
		for (Object arg : args) {
			this.args.add(arg);
		}
		return this;
	}
	
	/**
	 * <p>
	 * String Arrays
	 * </p>
	 * @param args
	 * @return this
	 */
	public ProtoBuilder array(String... args) {
		type = Protocol.Type.ARRAY;
		this.args.clear();
		for (Object arg : args) {
			this.args.add(arg);
		}
		return this;
	}
	
	/**
	 * <p>
	 * Arrays
	 * </p>
	 * @param args
	 * @return this
	 */
	public ProtoBuilder array(Object... args) {
		type = Protocol.Type.ARRAY;
		this.args.clear();
		for (Object arg : args) {
			this.args.add(arg);
		}
		return this;
	}
	
	/**
	 * <p>
	 * Build current protocol to bytes
	 * </p>
	 * @return RESP
	 */
	public byte[] build() {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			switch (type) {
				case Protocol.Type.STRING:
					bao.write(Protocol.BTS_SPECIFIER_STRING);
					bao.write(StringUtils.getBytes(String.valueOf(args.get(0)), charset));
					bao.write(Protocol.BTS_CRLF);
					break;
				case Protocol.Type.ERROR:
					bao.write(Protocol.BTS_SPECIFIER_ERROR);
					bao.write(StringUtils.getBytes(String.valueOf(args.get(0)), charset));
					bao.write(Protocol.BTS_CRLF);
					break;
				case Protocol.Type.INTEGER:
					bao.write(Protocol.BTS_SPECIFIER_INTEGER);
					bao.write(StringUtils.getBytes(String.valueOf(args.get(0)), charset));
					bao.write(Protocol.BTS_CRLF);
					break;
				case Protocol.Type.BULK_STRING:
					if (args.get(0) == null) {
						bao.write(Protocol.BTS_SPECIFIER_BULK);
						bao.write(StringUtils.getBytes(String.valueOf(-1), charset));
						bao.write(Protocol.BTS_CRLF);
					} else {
						byte[] bts = StringUtils.getBytes((String) args.get(0), charset);
						bao.write(Protocol.BTS_SPECIFIER_BULK);
						bao.write(StringUtils.getBytes(String.valueOf(bts.length), charset));
						bao.write(Protocol.BTS_CRLF);
						bao.write(bts);
						bao.write(Protocol.BTS_CRLF);
					}
					break;
				case Protocol.Type.ARRAY:
					bao.write(Protocol.BTS_SPECIFIER_ARRAY);
					bao.write(StringUtils.getBytes(String.valueOf(args.size()), charset));
					bao.write(Protocol.BTS_CRLF);
					for (Object arg : args) {
						if (arg == null) {
							bao.write(Protocol.BTS_SPECIFIER_BULK);
							bao.write(StringUtils.getBytes(String.valueOf(-1), charset));
							bao.write(Protocol.BTS_CRLF);
						} else if (arg instanceof String) {
							String value = (String) arg;
							byte[] bts = StringUtils.getBytes(value, charset);
							bao.write(Protocol.BTS_SPECIFIER_BULK);
							bao.write(StringUtils.getBytes(String.valueOf(bts.length), charset));
							bao.write(Protocol.BTS_CRLF);
							bao.write(bts);
							bao.write(Protocol.BTS_CRLF);
						} else if (arg instanceof Integer || arg instanceof Long) {
							bao.write(Protocol.BTS_SPECIFIER_INTEGER);
							bao.write(StringUtils.getBytes(String.valueOf(arg), charset));
							bao.write(Protocol.BTS_CRLF);
						} else if (arg instanceof int[]) {
							bao.write(new ProtoBuilder().setCharset(charset).array((int[])arg).build());
						} else if (arg instanceof long[]) {
							bao.write(new ProtoBuilder().setCharset(charset).array((long[])arg).build());
						} else if (arg instanceof Integer[]) {
							bao.write(new ProtoBuilder().setCharset(charset).array((Integer[])arg).build());
						} else if (arg instanceof Long[]) {
							bao.write(new ProtoBuilder().setCharset(charset).array((Long[])arg).build());
						} else if (arg instanceof String[]) {
							bao.write(new ProtoBuilder().setCharset(charset).array((String[])arg).build());
						} else if (arg instanceof ProtoBuilder) {
							bao.write(((ProtoBuilder)arg).build());
						} else if (arg instanceof Enum) {
							byte[] bts = StringUtils.getBytes(arg.toString(), charset);
							bao.write(Protocol.BTS_SPECIFIER_BULK);
							bao.write(StringUtils.getBytes(String.valueOf(bts.length), charset));
							bao.write(Protocol.BTS_CRLF);
							bao.write(bts);
							bao.write(Protocol.BTS_CRLF);
						} else {
							throw new RuntimeException("arg:" + arg.getClass() + " not support!");
						}
					}
					break;
				default:
					throw new RuntimeException("Unkown build type:" + type);
			}
			return bao.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Unkown build type:" + e.getMessage(), e);
		}
	}
	
}