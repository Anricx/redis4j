package com.gmail.dengtao.joe.redis4j.proto;

import org.junit.Assert;
import org.junit.Test;

import com.gmail.dengtao.joe.redis4j.proto.ProtoBuilder;
import com.gmail.dengtao.joe.redis4j.proto.Protocol;

public class ProtoBuilderTest {

	@Test
	public void test() {
		Assert.assertEquals(new ProtoBuilder().string("RESP Simple Strings").build(), "+RESP Simple Strings\r\n");
		Assert.assertEquals(new ProtoBuilder().string("").build(), "+\r\n");
		Assert.assertEquals(new ProtoBuilder().error("Error message").build(), "-Error message\r\n");
		Assert.assertEquals(new ProtoBuilder().integer(10000).build(), ":10000\r\n");
		Assert.assertEquals(new ProtoBuilder().bulk("foobar").build(), "$6\r\nfoobar\r\n");
		Assert.assertEquals(new ProtoBuilder().bulk("").build(), "$0\r\n\r\n");
		Assert.assertEquals(new ProtoBuilder().bulk(null).build(), "$-1\r\n");
		Assert.assertEquals(new ProtoBuilder().array("foo", null, "bar").build(), "*3\r\n$3\r\nfoo\r\n$-1\r\n$3\r\nbar\r\n");
		int[] data = {1, 2, 4, 6};
		String[] strs = {"A", "B", "C"};
		
		Assert.assertEquals(new ProtoBuilder().array("foo", null, data, "bar", strs, new ProtoBuilder().error("Error message")).build(), "*6\r\n$3\r\nfoo\r\n$-1\r\n*4\r\n:1\r\n:2\r\n:4\r\n:6\r\n$3\r\nbar\r\n*3\r\n$1\r\nA\r\n$1\r\nB\r\n$1\r\nC\r\n-Error message\r\n");
		
		System.out.println(
				new ProtoBuilder().array(Protocol.Command.ECHO, System.currentTimeMillis()).build()
		);
		
	}

}