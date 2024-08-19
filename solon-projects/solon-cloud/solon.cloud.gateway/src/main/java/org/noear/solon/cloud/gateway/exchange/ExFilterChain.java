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
package org.noear.solon.cloud.gateway.exchange;

import reactor.core.publisher.Mono;

/**
 * 交换过滤器链
 *
 * @author noear
 * @since 2.9
 */
@FunctionalInterface
public interface ExFilterChain {
    /**
     * 过滤
     *
     * @param ctx 交换上下文
     */
    Mono<Void> doFilter(ExContext ctx);
}