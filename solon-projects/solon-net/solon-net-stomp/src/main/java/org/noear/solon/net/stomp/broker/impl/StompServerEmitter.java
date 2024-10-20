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
package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.net.stomp.*;

/**
 * Stomp 服务端发射器
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public class StompServerEmitter implements StompEmitter {
    private final StompServerOperations operations;

    protected StompServerEmitter(StompServerOperations operations) {
        this.operations = operations;
    }

    private void sendToSessionDo(StompSession session, SubscriptionInfo subscription, String destination, Message message) {
        if (subscription != null) {
            Frame replyMessage = Frame.newBuilder()
                    .command(Commands.MESSAGE)
                    .payload(message.getPayload())
                    .headerAdd(message.getHeaderAll())
                    .headerSet(Headers.DESTINATION, destination)
                    .headerSet(Headers.SUBSCRIPTION, subscription.getSubscriptionId())
                    .headerSet(Headers.MESSAGE_ID, Utils.guid())
                    .build();

            session.send(replyMessage);
        }
    }

    @Override
    public void sendToSession(StompSession session, String destination, Message message) {
        SubscriptionInfo subscription = ((StompSessionImpl) session).getSubscription(destination);
        sendToSessionDo(session, subscription, destination, message);
    }

    @Override
    public void sendToUser(String user, String destination, Message message) {
        KeyValues<StompSession> sessions = operations.getSessionNameMap().get(user);

        for (StompSession s1 : sessions.getValues()) {
            sendToSession(s1, destination, message);
        }
    }


    /**
     * 发送到目的地
     *
     * @param destination 目标（支持模糊匹配，如/topic/**）
     * @param message     消息
     */
    @Override
    public void sendTo(String destination, Message message) {
        assert message != null;

        if (Utils.isEmpty(destination)) {
            return;
        }

        operations.getSubscriptions().parallelStream()
                .filter(subscription -> subscription.matches(destination))
                .forEach(subscription -> {
                    StompSession session = operations.getSessionIdMap().get(subscription.getSessionId());

                    if (session != null) {
                        sendToSessionDo(session, subscription, destination, message);
                    }
                });
    }
}