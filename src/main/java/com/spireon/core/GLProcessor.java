package com.spireon.core;

import com.spireon.core.handlers.HttpHandler;
import com.spireon.core.handlers.ProducerHandler;
import com.spireon.core.kafka.KafkaSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class GLProcessor {

    public static String APP_NAME = "GLProcessor";
    private static final Logger LOGGER = LoggerFactory.getLogger(GLProcessor.class);

    private static String kafkaHost;
    private static String kafkaPort;

    private static String topicSource;
    private static String topicDestination;

    private static String groupId;

    private static String identityUrl;
    private static String identityUsername;
    private static String identityPassword;
    private static String identityToken;

    private static String glServiceUrl;
    private static String tokenTime;

    private Properties prop = new Properties();
    private InputStream input = null;

    public GLProcessor() {
        try {
            String dir = System.getProperty("user.dir");
            System.out.println(dir);

            String propertiesFile = GLProcessor.class.getClassLoader().getResource("globalLandmark.properties").getFile();

            // For production the properties file is always expctected in the /etc/flink directoy
            if (propertiesFile.indexOf(".jar") >= 0) {
                propertiesFile = "globalLandmark.properties";
            }

            input = new FileInputStream(propertiesFile);

            // load a properties file
            prop.load(input);

            // get the property
            identityUrl = prop.getProperty("identity.service.base.url");
            identityUsername = prop.getProperty("identity.user.name");
            identityPassword= prop.getProperty("identity.user.password");
            identityToken = prop.getProperty("identity.app.token");

            kafkaHost = prop.getProperty("kafka.host");
            kafkaPort = prop.getProperty("kafka.port");
            topicSource = prop.getProperty("kafka.consumer.topic");
            topicDestination = prop.getProperty("kafka.producer.topic");

            groupId = prop.getProperty("impound-lots.groupId");

            glServiceUrl = prop.getProperty("globalLandmark.service.url");
            tokenTime = prop.getProperty("globalLandmark.tokengeneration.time");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        HttpHandler httpHandler = new HttpHandler(glServiceUrl, identityUrl, identityUsername, identityPassword, identityToken);

        // Kafka consumer
        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", kafkaHost + ":" + kafkaPort);
        consumerProperties.put("group.id", groupId);
        consumerProperties.put("auto.offset.reset", "latest");
        consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaSource kafkaClient = new KafkaSource(consumerProperties);


        // Kafka producer
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", kafkaHost + ":" + kafkaPort);
        producerProperties.put("key.serializer",     "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        ProducerHandler producerHandler = new ProducerHandler(producerProperties, topicDestination, httpHandler);


        // Hookup consumer to call producer when message received
        kafkaClient.process(producerHandler);
        kafkaClient.subscribe(Arrays.asList(topicSource));
    }

    public static void main(String[] args) {
        new GLProcessor();

    }
}
