package com.gmail.dengtao.joe.redis4j.proto;

import org.junit.Assert;
import org.junit.Test;

public class ProtoBuilderTest {

	@Test
	public void test() {
		Assert.assertArrayEquals(new ProtoBuilder().string("RESP Simple Strings").build(), "+RESP Simple Strings\r\n".getBytes());
		Assert.assertArrayEquals(new ProtoBuilder().string("").build(), "+\r\n".getBytes());
		Assert.assertArrayEquals(new ProtoBuilder().error("Error message").build(), "-Error message\r\n".getBytes());
		Assert.assertArrayEquals(new ProtoBuilder().integer(10000).build(), ":10000\r\n".getBytes());
		Assert.assertArrayEquals(new ProtoBuilder().bulk("foobar").build(), "$6\r\nfoobar\r\n".getBytes());
		Assert.assertArrayEquals(new ProtoBuilder().bulk("").build(), "$0\r\n\r\n".getBytes());
		Assert.assertArrayEquals(new ProtoBuilder().bulk(null).build(), "$-1\r\n".getBytes());
		Assert.assertArrayEquals(new ProtoBuilder().array("foo", null, "bar").build(), "*3\r\n$3\r\nfoo\r\n$-1\r\n$3\r\nbar\r\n".getBytes());
		int[] data = {1, 2, 4, 6};
		String[] strs = {"A", "B", "C"};
		//Integer[] data = {1, 2, 4, 6};
		
		System.out.println(new String(new ProtoBuilder().array("foo", null, data, "bar", strs, new ProtoBuilder().error("Error message")).build()));
		
		Assert.assertArrayEquals(new ProtoBuilder().array("foo", null, data, "bar", strs, new ProtoBuilder().error("Error message")).build(), "*6\r\n$3\r\nfoo\r\n$-1\r\n*4\r\n:1\r\n:2\r\n:4\r\n:6\r\n$3\r\nbar\r\n*3\r\n$1\r\nA\r\n$1\r\nB\r\n$1\r\nC\r\n-Error message\r\n".getBytes());
	}

}