package com.gmail.dengtao.joe.redis4j.proto;

import java.util.ArrayList;
import java.util.List;

import com.gmail.dengtao.joe.redis4j.exception.RedisException;
import com.gmail.dengtao.joe.redis4j.exception.RedisProtoNotReadyException;
import com.gmail.dengtao.joe.redis4j.utils.ByteUtils;
import com.gmail.dengtao.joe.redis4j.utils.NumberUtils;
import com.gmail.dengtao.joe.redis4j.utils.StringUtils;

/**
 * Redis protocol parset. designed for deserialize RESP to object.
 * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
 * @version 1.0
 */
public class ProtoParser {
	
	private static final Object[] EMPTY_ARRAY = new Object[0]; 
	
	private int type;
	private String charset = Protocol.DEFAULT_CHARSET; // charset
	private int position;
	private byte[] current;	// for string, int, bulk, error
	private Object[] array; // for array only!

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
	public ProtoParser setCharset(String charset) {
		this.charset = charset;
		return this;
	}
	
	/**
	 * Try to parse object FROM RESP bytes.
	 * @param buf RESP bytes
	 * @return this
	 */
	public ProtoParser parse(byte[] buf) {
		type = 0;
		position = -1;
		array = null;
		if (ByteUtils.startWith(buf, (byte) Protocol.SPECIFIER_STRING)) {
			int index = -1;
			if ((index = ByteUtils.indexOf(buf, Protocol.BTS_CRLF)) == -1) {
				throw new RedisProtoNotReadyException("buf not ready, waiting CRLF....");
			}
			type = Protocol.Type.STRING;
			current = ByteUtils.cut(buf, 1, index - 1);
			position = index + Protocol.BTS_CRLF.length;
		} else if (ByteUtils.startWith(buf, (byte) Protocol.SPECIFIER_ERROR)) {
			int index = -1;
			if ((index = ByteUtils.indexOf(buf, Protocol.BTS_CRLF)) == -1) {
				throw new RedisProtoNotReadyException("buf not ready, waiting CRLF....");
			}
			type = Protocol.Type.ERROR;
			current = ByteUtils.cut(buf, 1, index - 1);
			position = index + Protocol.BTS_CRLF.length;
		} else if (ByteUtils.startWith(buf, (byte) Protocol.SPECIFIER_INTEGER)) {
			int index = -1;
			if ((index = ByteUtils.indexOf(buf, Protocol.BTS_CRLF)) == -1) {
				throw new RedisProtoNotReadyException("buf not ready, waiting CRLF....");
			}
			current = ByteUtils.cut(buf, 1, index - 1);
			if (NumberUtils.isInt(new String(current))) {
				type = Protocol.Type.INTEGER;
				position = index + Protocol.BTS_CRLF.length;
			} else {
				current = null;
				throw new RuntimeException("bulk string length invalid! maybe deep error?");
			}
		} else if (ByteUtils.startWith(buf, (byte) Protocol.SPECIFIER_BULK)) {
			int index = -1;
			if ((index = ByteUtils.indexOf(buf, Protocol.BTS_CRLF)) == -1) {
				throw new RedisProtoNotReadyException("buf not ready, waiting CRLF....");
			}
			int length = NumberUtils.toInt(new String(ByteUtils.cut(buf, 1, index - 1)), -2);
			if (length <= -2) {
				throw new RuntimeException("bulk string length invalid! maybe deep error?");
			} else if (length == -1) {	// null string
				type = Protocol.Type.BULK_STRING;
				current = null;
				position = index + Protocol.BTS_CRLF.length;
			} else {	// empty string
				position = index + Protocol.BTS_CRLF.length;
				byte[] tmp = ByteUtils.cut(index + Protocol.BTS_CRLF.length, buf);
				if ((index = ByteUtils.indexOf(tmp, Protocol.BTS_CRLF)) == -1) {
					if (tmp.length >= length + 4) {
						throw new RuntimeException("bulk string length invalid! maybe deep error?");
					} else {
						throw new RedisProtoNotReadyException("buf not ready, waiting CRLF....");
					}
				}
				type = Protocol.Type.BULK_STRING;
				current = ByteUtils.cut(tmp, index);
				position = position + index + Protocol.BTS_CRLF.length;
			}
		} else if (ByteUtils.startWith(buf, (byte) Protocol.SPECIFIER_ARRAY)) {
			int index = -1;
			if ((index = ByteUtils.indexOf(buf, Protocol.BTS_CRLF)) == -1) {
				throw new RedisProtoNotReadyException("buf not ready, waiting CRLF....");
			}
			int size = NumberUtils.toInt(new String(ByteUtils.cut(buf, 1, index - 1)), -1);
			if (size < 0) {
				throw new RuntimeException("array size invalid! maybe deep error?");
			} else if (size == 0) {	// empty array
				type = Protocol.Type.ARRAY;
				array = EMPTY_ARRAY;
				position = index + Protocol.BTS_CRLF.length;
			} else {
				try {
					position = index + Protocol.BTS_CRLF.length;
					List<Object> list = new ArrayList<Object>(size);
					for (int i = 0; i < size; i++) {
						byte[] tmp = ByteUtils.cut(position, buf);	// array body
						ProtoParser parser = new ProtoParser().parse(tmp);
						list.add(parser.result());
						position += parser.position();
					}
					type = Protocol.Type.ARRAY;
					array = list.toArray(new Object[size]);
				} catch (RedisProtoNotReadyException e) {
					position = -1;
					throw e;
				} catch (RuntimeException e) {
					position = -1;
					throw e;
				}
			}
		}
		return this;
	}
	
	/**
	 * return current position after parse.
	 * @return -1 on failure or not parsed.
	 */
	public int position() {
		return position;
	}
	
	public Object result() {
		if (Protocol.Type.STRING == type) {
			return StringUtils.toString(current, charset);
		} else if (Protocol.Type.ERROR == type) {
			return new RedisException(StringUtils.toString(current, charset));
		} else if (Protocol.Type.INTEGER == type) {
			return NumberUtils.toInt(StringUtils.toString(current, charset));
		} else if (Protocol.Type.BULK_STRING == type) {
			return current == null ? null : StringUtils.toString(current, charset);
		} else if (Protocol.Type.ARRAY == type) {
			return array;
		} else {
			throw new RuntimeException("parser not ready, parse error or parse not called!");
		}
	}
}