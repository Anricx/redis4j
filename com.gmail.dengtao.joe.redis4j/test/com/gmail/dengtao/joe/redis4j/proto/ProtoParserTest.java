package com.gmail.dengtao.joe.redis4j.proto;

import org.junit.Test;

import com.gmail.dengtao.joe.redis4j.proto.ProtoBuilder;
import com.gmail.dengtao.joe.redis4j.proto.ProtoParser;

public class ProtoParserTest {

	@Test
	public void test() {
		byte[] data = new ProtoBuilder().array("foo", new String[]{"a", "b", "c"}, "bar").build();
		Object result = new ProtoParser().parse(data).result();
		if (result instanceof Object[]) {
			for (Object obj :(Object[]) result) {
				if (obj instanceof Object[]) {
					for (Object d :(Object[]) obj) {
						System.out.println("\t" + d);
					}
				} else {
					System.out.println(obj);
				}
			}
		}
	}

}
