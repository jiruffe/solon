package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Event;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqProducer implements Closeable {
    RocketmqConfig config;
    ClientServiceProvider serviceProvider;
    Producer producer;

    public RocketmqProducer(RocketmqConfig config) {
        this.config = config;
    }

    private void init(CloudProps cloudProps) throws ClientException {
        if (producer != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (producer != null) {
                return;
            }


            serviceProvider = ClientServiceProvider.loadService();

            ClientConfigurationBuilder builder = ClientConfiguration.newBuilder();

            //服务地址
            builder.setEndpoints(config.getServer());
            //账号密码
            if (Utils.isNotEmpty(config.getAccessKey())) {
                builder.setCredentialProvider(new StaticSessionCredentialsProvider(config.getAccessKey(), config.getSecretKey()));
            }

            //发送超时时间，默认3000 单位ms
            if (config.getTimeout() > 0) {
                builder.setRequestTimeout(Duration.ofMillis(config.getTimeout()));
            }

            ClientConfiguration configuration = builder.build();

            ProducerBuilder producerBuilder = serviceProvider.newProducerBuilder()
                    .setClientConfiguration(configuration);

            Solon.context().getBeanAsync(TransactionChecker.class, bean -> {
                producerBuilder.setTransactionChecker(bean);
            });

            producer = producerBuilder.build();

        } finally {
            Utils.locker().unlock();
        }
    }

    public Transaction beginTransaction() throws ClientException {
        return producer.beginTransaction();
    }

    public boolean publish(CloudProps cloudProps, Event event, String topic) throws ClientException {
        init(cloudProps);

        //普通消息发送。
        Message message = MessageUtil.buildNewMeaage(serviceProvider, event, topic);

        //发送消息，需要关注发送结果，并捕获失败等异常。
        SendReceipt sendReceipt = null;

        if (event.transaction() == null) {
            sendReceipt = producer.send(message);
        } else {
            Transaction transaction = event.transaction().getListener(RocketmqTransactionListener.class).getTransaction();
            sendReceipt = producer.send(message, transaction);
        }

        if (sendReceipt != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        if (producer != null) {
            producer.close();
        }
    }
}