package com.gmail.dengtao.joe.redis4j.filter;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.dengtao.joe.redis4j.exception.RedisProtoNotReadyException;
import com.gmail.dengtao.joe.redis4j.proto.ProtoParser;
import com.gmail.dengtao.joe.redis4j.proto.Protocol;
import com.gmail.dengtao.joe.redis4j.utils.ByteUtils;
import com.gmail.dengtao.joe.transport.SocketConnector;
import com.gmail.dengtao.joe.transport.filter.FilterAdapter;
import com.gmail.dengtao.joe.transport.filter.FilterEntity;
import com.gmail.dengtao.joe.transport.session.Session;

public class ProtoFilter extends FilterAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProtoFilter.class);
	
	private SocketConnector connector;
	private byte[] buffer = ByteUtils.EMPTY_BYTE_ARRAY;
	
	public ProtoFilter(SocketConnector connector) {
		this.connector = connector;
	}

	@Override
	public void dataReceived(FilterEntity nextEntity, Session session,
			Object data) throws Exception {
		buffer = ByteUtils.merge(buffer, (byte[]) data);
		// Check buffer
		if (Protocol.isValid(buffer)) {
			// specifier ready!
			ProtoParser parser = new ProtoParser();
			try {
				parser.parse(buffer);
				int position = parser.position();
				if (position < 0) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.error("[Redis][Proto][invalid position, maby deep error, disconnect!][DUMP:" + Arrays.toString(buffer) + "]");
					}
					connector.close();
					return;
				}
				// invoke current
				super.dataReceived(nextEntity, session, parser.result());
				// invoke remain
				buffer = ByteUtils.cut(position, buffer);
				if (buffer.length > 0) {
					this.dataReceived(nextEntity, session, ByteUtils.EMPTY_BYTE_ARRAY);
				}
			} catch (RedisProtoNotReadyException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("[Redis][Proto][protocol not ready, waite...][DUMP:" + Arrays.toString(buffer) + "]");
				}
			} catch (RuntimeException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("[Redis][Proto][protocol parse failed, disconnect!][DUMP:" + Arrays.toString(buffer) + "]");
				}
				connector.close();
				return;
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error("[Redis][Proto][invalid protocol data, disconnect!][DUMP:" + Arrays.toString(buffer) + "]");
			}
			connector.close();
			return;
		}
	}

}