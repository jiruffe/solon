/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.net.stomp.handle;

import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.StompEmitter;
import org.noear.solon.net.stomp.Headers;
import org.noear.solon.net.websocket.WebSocket;

import java.io.IOException;

/**
 * Stomp 通用上下文适配
 *
 * @author noear
 * @since 3.0
 */
public class StompContext extends ContextEmpty {
    private WebSocket session;
    private Frame frame;
    private String destination;
    private StompEmitter emitter;

    public StompContext(WebSocket session, Frame frame, String destination, StompEmitter emitter) {
        this.session = session;
        this.frame = frame;
        this.destination = destination;
        this.emitter = emitter;

        //指定返回处理
        attrSet(org.noear.solon.core.Constants.ATTR_RETURN_HANDLER, StompReturnHandler.getInstance());
    }

    /**
     * 数据帧
     */
    public Frame frame() {
        return frame;
    }

    /**
     * 发射器
     */
    public StompEmitter emitter() {
        return emitter;
    }

    /**
     * 请求对象
     */
    @Override
    public Object request() {
        return session;
    }

    /**
     * 会话Id
     */
    @Override
    public String sessionId() {
        return session.id();
    }

    /**
     * 请求方式
     */
    @Override
    public String method() {
        return MethodType.MESSAGE.name;
    }

    /**
     * 请求路径
     */
    @Override
    public String path() {
        return destination;
    }

    /**
     * 内容类型
     */
    @Override
    public String contentType() {
        return frame.getHeader(Headers.CONTENT_TYPE);
    }

    /**
     * 请求主体
     *
     * @param charset 字符集
     */
    @Override
    public String body(String charset) throws IOException {
        return frame.getPayload();
    }

    /**
     * 请求头
     */
    @Override
    public MultiMap<String> headerMap() {
        if (headerMap == null) {
            headerMap = new MultiMap<>();

            for (KeyValue<String> kv : frame.getHeaderAll()) {
                headerMap.add(kv.getKey(), kv.getValue());
            }
        }

        return headerMap;
    }

    /**
     * 提取
     */
    @Override
    public Object pull(Class<?> clz) {
        if (Frame.class.isAssignableFrom(clz)) {
            return frame;
        }

        if (WebSocket.class.isAssignableFrom(clz)) {
            return session;
        }

        return null;
    }
}