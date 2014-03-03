package com.gmail.dengtao.joe.redis4j;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;

import com.gmail.dengtao.joe.redis4j.exception.RedisConnectionException;
import com.gmail.dengtao.joe.redis4j.exception.RedisException;
import com.gmail.dengtao.joe.redis4j.exception.RedisIOException;
import com.gmail.dengtao.joe.redis4j.exception.RedisTimeoutException;

public class RedisTest {

	@Test
	public void test() throws RedisConnectionException, IOException, InterruptedException, RedisException {
		Redis redis = new Redis("192.168.16.231", 6379, "llt660");
		redis.setIdleTime(100);
		Assert.assertTrue(redis.select(15));
		Assert.assertTrue(redis.set("key", "中文测试TEST"));
		Assert.assertTrue(redis.exists("key"));
		Assert.assertTrue(redis.append("key", "_add"));
		Assert.assertTrue(redis.ping() != -1);
		Assert.assertNotNull(redis.get("key"));
		Assert.assertTrue(redis.del("key"));
		Assert.assertNull(redis.get("key"));
		Assert.assertTrue(redis.set("key", "value", 10000));
		redis.close();
	}

	private static Redis redis;
	
	static {
		try {
			redis = new Redis("192.168.16.231", 6379, "llt660");
		} catch (RedisConnectionException e) {
			e.printStackTrace();
		};
	}
	
    private static final LinkedBlockingQueue<Long> setRunTimes = new LinkedBlockingQueue<Long>();
    private static final LinkedBlockingQueue<String> keys = new LinkedBlockingQueue<String>();
	private static volatile int tasks = 10000;

	public static void main(String[] args) throws RedisException, IOException {
		redis.select(0);
		redis.setTimeout(5000);
		redis.flush();
		
		//synchronized (lock) {
			//try {
				for (int i = 0; i < tasks; i++) {
					doSet();
					//exe.submit(new SetTask());
				}
				// lock.wait();
				System.out.println("All Down!");
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//}
		//}
		long total = 0;
		for (long time : setRunTimes) {
			total += time;
		}
		System.out.println("Total: " + total + "ns, Avg:" + (total / 10000.0 / 1000.0 / 1000.0) + "ms");
		redis.close();
	}
	
	private static synchronized void doSet() {
		String key = RandomUtils.random(36, true, true);
		String val = RandomUtils.random(12, true, true);
		try {
			 long startTime = System.nanoTime(); // ... the code being measured ... 
			 redis.set(key, val);
			 long estimatedTime = System.nanoTime() - startTime; 
			 setRunTimes.add(estimatedTime);
			 System.out.println("SET " + key + "=>" + val + " in " + estimatedTime + " ns");
		} catch (RedisConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RedisIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RedisTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RedisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keys.add(key);
		/*synchronized (lock) {
			tasks --;
			if (tasks <= 0) {
					lock.notifyAll();
			}
		}*/
	}
	
	class SetTask implements Runnable {

		@Override
		public void run() {
			doSet();
		}
	}
	
}