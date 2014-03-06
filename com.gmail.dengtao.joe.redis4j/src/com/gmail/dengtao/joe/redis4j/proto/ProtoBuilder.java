package com.gmail.dengtao.joe.redis4j.proto;

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
	 * String Arrays
	 * </p>
	 * @param args
	 * @return this
	 */
	public ProtoBuilder array(String[] args) {
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
	 * Build current protocol to string
	 * </p>
	 * @return RESP
	 */
	public String build() {
		StringBuffer sb = new StringBuffer();
		switch (type) {
			case Protocol.Type.STRING:
				sb.append(Protocol.SPECIFIER_STRING).append(args.get(0)).append(Protocol.CRLF);
				break;
			case Protocol.Type.ERROR:
				sb.append(Protocol.SPECIFIER_ERROR).append(args.get(0)).append(Protocol.CRLF);
				break;
			case Protocol.Type.INTEGER:
				sb.append(Protocol.SPECIFIER_INTEGER).append(args.get(0)).append(Protocol.CRLF);
				break;
			case Protocol.Type.BULK_STRING:
				if (args.get(0) == null) {
					sb.append(Protocol.SPECIFIER_BULK).append(-1).append(Protocol.CRLF);
				} else {
					String value = (String) args.get(0);
					byte[] bts = StringUtils.getBytes(value, charset);
					sb.append(Protocol.SPECIFIER_BULK).append(bts).append(Protocol.CRLF).append(value).append(Protocol.CRLF);
				}
				break;
			case Protocol.Type.ARRAY:
				sb.append(Protocol.SPECIFIER_ARRAY).append(args.size()).append(Protocol.CRLF);
				for (Object arg : args) {
					if (arg == null) {
						sb.append(Protocol.SPECIFIER_BULK).append(-1).append(Protocol.CRLF);
					} else if (arg instanceof String) {
						sb.append(Protocol.SPECIFIER_BULK).append(StringUtils.getBytes((String) arg, charset).length).append(Protocol.CRLF).append(arg).append(Protocol.CRLF);
					} else if (arg instanceof Integer) {
						sb.append(Protocol.SPECIFIER_INTEGER).append(arg).append(Protocol.CRLF);
					} else if (arg instanceof Long) {
						sb.append(Protocol.SPECIFIER_INTEGER).append(arg).append(Protocol.CRLF);
					} else if (arg instanceof int[]) {
						sb.append(new ProtoBuilder().array((int[])arg).build());
					} else if (arg instanceof long[]) {
						sb.append(new ProtoBuilder().array((long[])arg).build());
					} else if (arg instanceof String[]) {
						sb.append(new ProtoBuilder().array((String[])arg).build());
					} else if (arg instanceof ProtoBuilder) {
						sb.append(((ProtoBuilder)arg).build());
					} else if (arg instanceof Enum) {
						String value = arg.toString();
						sb.append(Protocol.SPECIFIER_BULK).append(value.length()).append(Protocol.CRLF)
						.append(value).append(Protocol.CRLF);
					} else {
						throw new RuntimeException("arg:" + arg.getClass() + " not support!");
					}
				}
				break;
			default:
				throw new RuntimeException("Unkown build type:" + type);
		}
		return sb.toString();
	}
	
}