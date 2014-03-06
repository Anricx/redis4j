package com.gmail.dengtao.joe.redis4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.dengtao.joe.redis4j.exception.RedisConnectionException;
import com.gmail.dengtao.joe.redis4j.exception.RedisException;
import com.gmail.dengtao.joe.redis4j.exception.RedisIOException;
import com.gmail.dengtao.joe.redis4j.exception.RedisTimeoutException;
import com.gmail.dengtao.joe.redis4j.filter.ProtoFilter;
import com.gmail.dengtao.joe.redis4j.proto.ProtoBuilder;
import com.gmail.dengtao.joe.redis4j.proto.Protocol;
import com.gmail.dengtao.joe.redis4j.utils.StringUtils;
import com.gmail.dengtao.joe.transport.SocketConnector;
import com.gmail.dengtao.joe.transport.handler.HandlerAdapter;
import com.gmail.dengtao.joe.transport.session.IdleStatus;
import com.gmail.dengtao.joe.transport.session.Session;

/**
 * <p>
 * Minimalist redis client implementation.
 * </p>
 * <b> Core Methods</b>
 * <pre>
 * {@link #select(int)} to select redis database.
 * {@link #set(String, String)} set key to hold the string value. 
 * {@link #set(String, String, long)} set key to hold the string value and specified expire time. 
 * {@link #append(String, String)} appends the value at the end of the string.
 * {@link #get(String)} get the value of key.
 * {@link #del(String)} to delete a key.
 * {@link #exists(String)} to check key exists.
 * {@link #ping()} to test if a connection is still alive, or to measure latency.
 * {@link #close()} to close redis connection.
 * </pre>
 * <b>To custom deep socket</b>
 * <pre>
 * All in {@link #getConnector()}.
 * </pre>
 * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
 * @version 1.0
 */
public class Redis implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Redis.class);
	/** Connection information */
	private final String host;
	private final int port;
	private final String password;
	private String charset = Protocol.DEFAULT_CHARSET;
	/** connection reconnect timeout */
	private long reconnect = 5000;
	/** connection idle timeout */
	private long idleTime = 720000;
	/** redis query timeout */
	private long timeout = 30000;
	
	/** Redis connection */
	private volatile boolean active = true;
	private volatile Session connection = null;
	private final Object lock = this;
	
	/** Socket Connect.... */
	private final SocketConnector connector = new SocketConnector();
	private final MessageHandler handler = new MessageHandler();
	
	/**
	 * instance a redis client.
	 * @param host	redis host
	 * @param port  redis port
	 * @param password redis password
	 */
	public Redis(String host, int port, String password) {
		if (host == null || password == null || port < 1 || port > 65535) throw new IllegalArgumentException();
		this.host = host; this.port = port; this.password = password;
		this.connect(this.host, this.port, this.password);
	}
	
	/**
	 * do redis connect...
	 * @param host
	 * @param port
	 * @param password
	 */
    private void connect(String host, int port, String password) {
    	// Close current
    	if (connection != null) {
			try { this.close(); } catch (Exception cause) { /* nothing */ }
    	}
    	synchronized (lock) {
    		active = true;
    		new RedisConnectionThread(host, port, lock).start();
    		// waite channel open
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Connect][waite connection open...]");
			}
			try { lock.wait(); } catch (Exception cause) { /* nothing */ }
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Connect][connection opened]");
			}
			// waite session open and auth
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Connect][waite session open...]");
			}
			try { lock.wait(); } catch (Exception cause) { /* nothing */ }
			if (LOGGER.isDebugEnabled()) {
				if (connection == null) {	// session open failed
					LOGGER.error("[Redis][Connect][session open failed...]");
				} else {
					LOGGER.debug("[Redis][Connect][session opened]");
				}
			}
		}
    }
    
    /**
     * Select the DB with having the specified zero-based numeric index. 
     * New connections always use DB 0.
     * @param index
     * @return
     * @throws RedisConnectionException 
     * @throws RedisIOException 
     * @throws RedisTimeoutException 
     */
    public boolean select(int index) throws RedisConnectionException, RedisIOException, RedisException, RedisTimeoutException {
    	if (index < 0) throw new IllegalArgumentException();
    	if (connection == null) throw new RedisConnectionException("server not connect yet!");
    	try {
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Select][try to select db:" + index + "]");
			}
    		RedisResult result = handler.request(new ProtoBuilder().array(Protocol.Command.SELECT, String.valueOf(index)).build(), timeout);
    		if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Select][select db:" + (result.getResponse() == null ? "error" : result.getResponse()) + "]");
			}
    		return result.getResponse() == null ? false : true;
    	} catch (RedisConnectionException e) {
    		throw e;
    	} catch (RedisTimeoutException e) {
    		throw e;
    	} catch (RedisIOException e) {
    		throw e;
    	} catch (RedisException e) {
    		throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
    }

	/**
     * Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type.
     * @param key
     * @param val
     * @return return true on success
     * @throws RedisConnectionException 
	 * @throws RedisTimeoutException 
     */
    public boolean set(String key, String val) throws RedisConnectionException, RedisIOException, RedisException, RedisTimeoutException {
    	if (StringUtils.isBlank(key)) throw new IllegalArgumentException();
    	if (connection == null) throw new RedisConnectionException("server not connect yet!");
    	try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Set][try to set [" + key + "=>" + val + "]]");
			}
			RedisResult result = handler.request(new ProtoBuilder().setCharset(charset).array(Protocol.Command.SET, key, val).build(), timeout);
			if (result == null) {
    			throw new RedisTimeoutException();
    		}
			if (result.getException() != null) {
				throw result.getException();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Set][set [" + key + "=>" + val + "] " + (result.getResponse() == null ? "error" : result.getResponse()) + "]");
			}
			return result.getResponse() == null ? false : true;
    	} catch (RedisConnectionException e) {
    		throw e;
    	} catch (RedisTimeoutException e) {
    		throw e;
    	} catch (RedisIOException e) {
    		throw e;
    	} catch (RedisException e) {
    		throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
	}
    
    /**
     * Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type.
     * @param key
     * @param val
     * @param expire Set the specified expire time, in milliseconds.
     * @return return true on success
     * @throws RedisTimeoutException 
     */
    public boolean set(String key, String val, long expire) throws RedisConnectionException, RedisIOException, RedisException, RedisTimeoutException {
    	if (StringUtils.isBlank(key)) throw new IllegalArgumentException();
    	if (connection == null) throw new RedisConnectionException("server not connect yet!");
    	try {
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Set][try to set [" + key + "=>" + val + ", expire in " + expire + " ms]]");
			}
			RedisResult result = handler.request(new ProtoBuilder().setCharset(charset).array(Protocol.Command.SET, key, val, "PX", String.valueOf(expire)).build(), timeout);
			if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Set][set [" + key + "=>" + val + ", expire in " + expire + " ms] " + (result.getResponse() == null ? "error" : result.getResponse()) + "]");
			}
    		return result.getResponse() == null ? false : true;
    	} catch (RedisConnectionException e) {
    		throw e;
    	} catch (RedisTimeoutException e) {
    		throw e;
    	} catch (RedisIOException e) {
    		throw e;
    	} catch (RedisException e) {
    		throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
    }
    
    /**
	 * If key already exists and is a string,
	 * this command appends the value at the end of the string. 
	 * If key does not exist it is created and set as an empty string, 
	 * so APPEND will be similar to SET in this special case.
	 * @param key
	 * @param val
	 * @return
	 * @throws RedisTimeoutException 
	 */
	public boolean append(String key, String val) throws RedisConnectionException, RedisIOException, RedisException, RedisTimeoutException {
		if (StringUtils.isBlank(key)) throw new IllegalArgumentException();
		if (connection == null) throw new RedisConnectionException("server not connect yet!");
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Append][try to append [" + key + "=>>" + val + "]]");
			}
			RedisResult result = handler.request(new ProtoBuilder().setCharset(charset).array(Protocol.Command.APPEND, key, val).build(), timeout);
			if (result == null) {
				throw new RedisTimeoutException();
			}
			if (result.getException() != null) {
				throw result.getException();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Append][append [" + key + "=>>" + val + "] " + (result.getResponse() == null ? "error" : result.getResponse()) + "]");
			}
			return result.getResponse() == null ? false : true;
		} catch (RedisConnectionException e) {
			throw e;
		} catch (RedisTimeoutException e) {
			throw e;
		} catch (RedisIOException e) {
			throw e;
		} catch (RedisException e) {
			throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
	}

	/**
     * Get the value of key. 
     * If the key does not exist the special value nil is returned. 
     * @param key
     * @return
     * @throws RedisConnectionException 
     * @throws RedisIOException 
     * @throws RedisException 
     * @throws RedisTimeoutException 
     */
    public Object get(String key) throws RedisConnectionException, RedisIOException, RedisException, RedisTimeoutException {
    	if (connection == null) throw new RedisConnectionException("server not connect yet!");
    	try {
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Get][try to get [" + key + "]]");
			}
    		RedisResult result = handler.request(new ProtoBuilder().setCharset(charset).array(Protocol.Command.GET, key).build(), timeout);
    		if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Get][get [" + key + "=>" + result.getResponse() + "]]");
			}
    		return result.getResponse();
    	} catch (RedisConnectionException e) {
    		throw e;
    	} catch (RedisTimeoutException e) {
    		throw e;
    	} catch (RedisIOException e) {
    		throw e;
    	} catch (RedisException e) {
    		throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
    }
    
    /**
     * Removes the specified keys. 
     * A key is ignored if it does not exist.
     * @param key
     * @return
     * @throws RedisTimeoutException 
     */
    public boolean del(String key) throws RedisConnectionException, RedisIOException, RedisException, RedisTimeoutException {
    	if (StringUtils.isBlank(key)) throw new IllegalArgumentException();
    	if (connection == null) throw new RedisConnectionException("server not connect yet!");
    	try {
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Del][try to del [" + key + "]]");
			}
    		RedisResult result = handler.request(new ProtoBuilder().setCharset(charset).array(Protocol.Command.DEL, key).build(), timeout);
    		if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Del][del [" + key + "] " + ("1".equals(String.valueOf(result.getResponse())) ? true : false) + "]");
			}
    		return "1".equals(String.valueOf(result.getResponse())) ? true : false;
    	} catch (RedisConnectionException e) {
    		throw e;
    	} catch (RedisTimeoutException e) {
    		throw e;
    	} catch (RedisIOException e) {
    		throw e;
    	} catch (RedisException e) {
    		throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
    }
    
    /**
     * Returns if key exists.
     * @param key
     * @return
     * @throws RedisTimeoutException 
     */
    public boolean exists(String key) throws RedisConnectionException, RedisIOException, RedisException, RedisTimeoutException {
    	if (StringUtils.isBlank(key)) throw new IllegalArgumentException();
    	if (connection == null) throw new RedisConnectionException("server not connect yet!");
    	try {
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Exists][try to check [" + key + "] exists]");
			}
    		RedisResult result = handler.request(new ProtoBuilder().setCharset(charset).array(Protocol.Command.EXISTS, key).build(), timeout);
    		if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Exists][[" + key + "] " + ("1".equals(String.valueOf(result.getResponse())) ? "exists" : "not exists") + "]");
			}
    		return "1".equals(String.valueOf(result.getResponse())) ? true : false;
    	} catch (RedisConnectionException e) {
    		throw e;
    	} catch (RedisTimeoutException e) {
    		throw e;
    	} catch (RedisIOException e) {
    		throw e;
    	} catch (RedisException e) {
    		throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
    }
    
    /**
     * Delete all the keys of the currently selected DB.
     * @throws RedisConnectionException 
     * @throws RedisTimeoutException 
     * @throws RedisIOException 
     * @throws RedisException 
     * @return return true on success
     */
    public boolean flush() throws RedisConnectionException, RedisTimeoutException, RedisIOException, RedisException {
    	if (connection == null) throw new RedisConnectionException("server not connect yet!");
    	try {
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Flush][try to delete current db's all keys]");
			}
    		RedisResult result = handler.request(new ProtoBuilder().setCharset(charset).array(Protocol.Command.FLUSHDB).build(), timeout);
    		if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Flush][delete keys " + (result.getResponse() == null ? "failed" : "success") + "]");
			}
    		return result.getResponse() == null ? false : true;
    	} catch (RedisConnectionException e) {
    		throw e;
    	} catch (RedisTimeoutException e) {
    		throw e;
    	} catch (RedisIOException e) {
    		throw e;
    	} catch (RedisException e) {
    		throw e;
		} catch (Throwable e) {
			throw new RedisException(e);
		}
    }
    
    /**
     * Test if a connection is still alive, or to measure latency.
     * @return
     */
    public long ping() {
    	if (connection == null) return -1;
    	try {
    		long start = System.currentTimeMillis();
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Ping][try to ping server:" + start + "]");
			}
    		RedisResult result = handler.request(new ProtoBuilder().array(Protocol.Command.PING).build(), timeout);
    		if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Ping][ping finish in " + (System.currentTimeMillis() - start) + " ms]");
			}
    		return System.currentTimeMillis() - start;
		} catch (Throwable e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Ping][ping failed with:" + e.getMessage() + "]", e);
			}
			return -1;
		}
    }
    
	@Override
	public void close() throws IOException {
		if (connection == null) return;
		active = false;
    	try {
    		RedisResult result = handler.request(new ProtoBuilder().array(Protocol.Command.QUIT).build(), timeout);
    		if (result == null) {
    			throw new RedisTimeoutException();
    		}
    		if (result.getException() != null) {
				throw result.getException();
			}
    		connector.close();
		} catch (Throwable e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Ping][ping failed with:" + e.getMessage() + "]", e);
			}
			connector.close();
		}
	}

	/**
	 * Get current socket connector
	 * @return SocketConnector
	 */
    public SocketConnector getConnector() {
		return connector;
	}

	/**
     * Set charset for current redis connection.
     * @param charset charset name eg:UTF-8, ISO-8859-1
     */
    public void setCharset(String charset) {
    	Charset.forName(charset);
    	this.charset = charset;
    }
    
    /**
     * Get current redis connection charset;
     * @return charset name
     */
    public String getCharset() {
		return charset;
	}

    /**
     * Get current redis connection reconnect sleep time.
     * @return the length of time to reconnect sleep in milliseconds.
     */
	public long getReconnect() {
		return reconnect;
	}

	/**
	 * Set auto reconnect timeout.
	 * @param reconnect reconnect sleep time in milliseconds.
	 */
	public void setReconnect(long reconnect) {
		if (idleTime < 0) throw new IllegalArgumentException(">0 required!");
		this.reconnect = reconnect;
	}
	
	/**
	 * Get redis connection idle time for write.
	 * @return connection write idle time in milliseconds.
	 */
	public long getIdleTime() {
		return (connection == null) ? idleTime : connection.getIdleTime(IdleStatus.WRITE_IDLE);
	}

	/**
	 * Set redis connection write idle time.
	 * @param idleTime idle in milliseconds.
	 */
	public void setIdleTime(long idleTime) {
		if (idleTime < 0) throw new IllegalArgumentException(">=0 required!");
		this.idleTime = idleTime;
		if (connection != null) connection.setIdleTime(IdleStatus.WRITE_IDLE, this.idleTime);
	}
	
	/**
	 * Get redis connection query timeout.
	 * @return quert timeout in milliseconds.
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * Set redis connection quert timeout 
	 * @param timeout quert timeout in milliseconds.
	 */
	public void setTimeout(long timeout) {
		if (timeout < 0) throw new IllegalArgumentException(">=0 required!");
		this.timeout = timeout;
	}
	
	/*
	 * Redis connection thread, auto reconnect. 
	 * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
	 * @version 1.0
	 */
    class RedisConnectionThread extends Thread {

    	private final String host;
    	private final int port;
    	private final Object lock;
    	
		public RedisConnectionThread(String host, int port, Object lock) {
			super();
			this.setName("RedisConnectionThread");
			this.setDaemon(true);
			this.host = host;
			this.port = port;
			this.lock = lock;
			connector.getFilterChain().addLast("ProtoFilter", new ProtoFilter(connector));
		}

		@Override
		public void run() {
			while (active) {
				try {
					connector.init();
					connector.setHandler(handler);
					connector.open(host, port, lock);
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.error("[Redis][Connect][connection error:" + e.getMessage() + "]", e);
					}
					try { connector.close(); } catch (Exception cause) { /* nothing */ }
				}
				if (active) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("[Redis][Connect][server gone away! reconnect in " + reconnect + " ms]");
					}
					try { Thread.sleep(reconnect); } catch (Exception cause) { /* nothing */ }
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Connect][shutdown...]");
			}
		}
    	
    }
    
    /*
     * RedisConnectionHandler 
     * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
     * @version 1.0
     */
    class MessageHandler extends HandlerAdapter {
    	private Session session;
    	private Map<Object, RedisResult> results = new ConcurrentHashMap<Object, Redis.RedisResult>();
    	private Queue<Object> queue = new ConcurrentLinkedQueue<Object>();
    	
		@Override
		public void sessionOpened(Session session) throws Exception {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Connect][connection established.]");
			}
			this.session = session;
			if (password == null) {
				synchronized (lock) {
					connection = session;
					lock.notifyAll();
				}
				session.setIdleTime(IdleStatus.WRITE_IDLE, idleTime);
			} else {
				new RedisAuthThread(password).start();
			}
		}

		@Override
		public void sessionIdle(Session session, IdleStatus status)
				throws Exception {
			// Connection Idle, Heart Beat!
			session.send(new ProtoBuilder().setCharset(charset).array(Protocol.Command.ECHO, String.valueOf(System.currentTimeMillis())).build());
		}

		@Override
		public void dataReceived(Session session, Object data) throws Exception {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Receive][" + data + "]");
			}
			Object request = queue.poll();
			RedisResult result = null;
			if (data instanceof Exception) {
				result = new RedisResult((Throwable) data);
			} else {
				result = new RedisResult(session, data);
			}
			results.put(request, result);
			synchronized (request) {
				request.notifyAll();
			}
		}

		@Override
		public void dataSent(Session session, Object data) throws Exception {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Sent][" + new String((byte[]) data) + "]");
			}
			queue.offer(data);
		}

		@Override
		public void dataNotSent(Session session, Object data) throws Exception {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.warn("[Redis][NotSent][" + new String((byte[]) data) + "]");
			}
			RedisResult result = new RedisResult(new RedisIOException("request not sent:" + data));
			results.put(data, result);
			synchronized (data) {
				data.notifyAll();
			}
		}

		@Override
		public void exceptionCaught(Session session, Throwable cause) {
			if (cause instanceof ConnectException) {
				session.close();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("[Redis][Error][connection error:" + cause.getMessage() + "]", cause);
				}
				synchronized (lock) {
					connection = null;
					lock.notifyAll();
				}
			} else {
				LOGGER.error("[Redis][Error][" + cause.getMessage() + "][deep error?]", cause);
			}
		}

		@Override
		public void sessionClosed(Session session) throws Exception {
			this.session = null;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("[Redis][Connect][session closed...]");
			}
			// Notify All Exception
			for (Object request : queue) {
				RedisResult result = new RedisResult(new RedisConnectionException("connection closed!"));
				results.put(request, result);
				synchronized (request) {
					request.notifyAll();
				}
			}
			connection = null;
			this.session = null;
			this.queue.clear();
			this.results.clear();
		}
		
		private RedisResult request(byte[] request, long timeout) throws RedisConnectionException {
			if (session == null) {
				throw new RedisConnectionException("server not connect yet!");
			}
			synchronized (request) {
				session.send(request);
				try { request.wait(timeout); } catch (Exception cause) { /* nothing */ }
				return results.remove(request);
			}
		}
		
		/*
	     * Redis auth thread. Send auth command
	     * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
	     * @version 1.0
	     */
	    class RedisAuthThread extends Thread {

	    	private String password;
	    	
	    	public RedisAuthThread(String password) {
				super();
				this.password = password;
				this.setName("RedisAuthThread");
				this.setDaemon(true);
			}

			@Override
			public void run() {
				byte[] request = new ProtoBuilder().setCharset(charset).array(Protocol.Command.AUTH, password).build();
				try {
					RedisResult result = request(request, timeout);
					if (result == null) {
		    			throw new RedisTimeoutException();
		    		}
					if (result.getException() != null) {
						throw result.getException();
					}
					if (result.getConnection() == null) {
						throw new RuntimeException("result not valid! maybe deep error?");
					}
					synchronized (lock) {
						connection = result.getConnection();
						lock.notifyAll();
					}
					session.setIdleTime(IdleStatus.WRITE_IDLE, idleTime);
				} catch (Throwable e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.error("[Redis][Auth][auth failed with:" + e.getMessage() + "]", e);
					}
					synchronized (lock) {
						connection = null;
						lock.notifyAll();
					}
					connector.close();
				}
			}
	    }
	
    }
    
    /*
     * Redis Result
     * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
     * @version 1.0
     */
    class RedisResult {
    	
    	private Session connection;
    	private Object response;
    	private Throwable exception;

		public RedisResult(Session connection, Object response) {
			super();
			this.connection = connection;
			this.response = response;
		}

		public RedisResult(Throwable exception) {
			super();
			this.exception = exception;
		}

		public Object getResponse() {
			return response;
		}

		public Throwable getException() {
			return exception;
		}

		public Session getConnection() {
			return connection;
		}
    	
    }
    
}