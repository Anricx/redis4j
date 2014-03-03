package com.gmail.dengtao.joe.redis4j.proto;

import org.junit.Test;

import com.gmail.dengtao.joe.redis4j.proto.ProtoBuilder;
import com.gmail.dengtao.joe.redis4j.proto.ProtoParser;

public class ProtoParserTest {

	@Test
	public void test() {
		// byte[] buf = "+ERR invalid password\r\nabbbsdfdfb".getBytes();
		// new ProtoParser().parse(buf);
		// System.out.println(new ProtoBuilder().bulk("Test").build());
		// new ProtoParser().parse("$0\r\n\r\n".getBytes());
		//System.out.println(new ProtoBuilder().array("foo", null, "bar").build());
		//System.out.println("############");
		String data = new ProtoBuilder().array("foo", new String[]{"a", "b", "c"}, "bar").build() + "abc";
		Object result = new ProtoParser().parse(data.getBytes()).result();
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
