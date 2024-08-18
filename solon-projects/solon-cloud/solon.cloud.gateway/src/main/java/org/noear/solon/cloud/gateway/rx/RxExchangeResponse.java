package org.noear.solon.cloud.gateway.rx;

import io.vertx.core.buffer.Buffer;
import org.noear.solon.util.KeyValues;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 2.9
 */
public class RxExchangeResponse {
    private int status = 200;
    private Map<String, KeyValues<String>> headers = new LinkedHashMap<>();
    private Buffer body;

    public void status(int code) {
        this.status = code;
    }

    private KeyValues<String> getHeaderHolder(String key) {
        return headers.computeIfAbsent(key, k -> new KeyValues<>(key));
    }

    /**
     * 配置头
     */
    public RxExchangeResponse header(String key, String... values) {
        getHeaderHolder(key).setValues(values);
        return this;
    }

    /**
     * 配置头
     */
    public RxExchangeResponse header(String key, List<String> values) {
        getHeaderHolder(key).setValues(values.toArray(new String[values.size()]));
        return this;
    }

    /**
     * 添加头
     */
    public RxExchangeResponse headerAdd(String key, String value) {
        getHeaderHolder(key).addValue(value);
        return this;
    }

    /**
     * 配置主体
     */
    public RxExchangeResponse body(Buffer body) {
        this.body = body;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, KeyValues<String>> getHeaders() {
        return headers;
    }

    public Buffer getBody() {
        return body;
    }
}
