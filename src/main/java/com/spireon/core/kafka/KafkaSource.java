package com.spireon.core.kafka;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class KafkaSource {

    private UpdateListener updateListener = null;
    private KafkaConsumer<String, String> consumer = null;
    private boolean isSubscribed = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSource.class);

    public KafkaSource(Properties clientProperties) {
        init(clientProperties);
    }

    private void init(Properties properties) {
        this.consumer = new KafkaConsumer<>(properties);
    }

    public void subscribe(List<String> topics) {
        if (consumer != null) {
            consumer.subscribe(topics);
            LOGGER.info("Subscribed to topics: {}", StringUtils.join(topics, ", "));
            isSubscribed = true;
            while (isSubscribed) {
                ConsumerRecords<String, String> records = consumer.poll(100);

                for (ConsumerRecord<String, String> record : records) {

                    LOGGER.info("Received new device message on topic: {}", record.topic());

                    if (updateListener != null) {
                        updateListener.update(record.topic(), record.value());
                        consumer.commitAsync();
                    }
                }
            }
        }
    }

    public void close() {
        isSubscribed = false;
        if (consumer != null) {
            consumer.unsubscribe();
            consumer.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
    }

    public void process(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }
}
