package com.spireon.core.handlers;

import com.spireon.core.kafka.UpdateListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerHandler implements UpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerHandler.class);

    private KafkaProducer producer;
    private String topicDestination;
    private HttpHandler httpHandler;

    public ProducerHandler(Properties clientProperties, String topicDestination, HttpHandler httpHandler) {
        producer = new KafkaProducer(clientProperties);

        this.topicDestination = topicDestination;
        this.httpHandler = httpHandler;
    }

    /**
     * @param topic the kafka topic of the new message received
     * @param message the device message received
     * @return TRUE if messages was successfully processed and published to vmaas service, FALSE otherwise.
     */
    @Override
    public boolean update(String topic, String message) {
        org.json.simple.JSONObject alertingCommand = null;

        if (!StringUtils.isBlank(topic) && !StringUtils.isBlank(message)) {
            System.out.println("deviceMessage = " + message);

            JSONObject deviceMessage = new JSONObject(message);

            Long deviceId = Long.valueOf((Integer)deviceMessage.get("deviceId"));
            Long accountId = Long.valueOf((Integer)deviceMessage.get("accountId"));

            JSONObject location = deviceMessage.getJSONObject("location");

            double lat = (Double)location.get("lat");
            double lng = (Double)location.get("lng");

            JSONObject deviceEventWithLandmark = httpHandler.checkLocation(deviceId, accountId, lat, lng, deviceMessage);

            JSONArray landmarks = (JSONArray) deviceEventWithLandmark.get("globalLandmarks");
            if(landmarks != null && landmarks.length() > 0) {

                // Convert org.json.JSONObject to org.json.simple.JSONObject
                JSONParser parser=new JSONParser(); // this needs the "json-simple" library

                try {
                    org.json.simple.JSONObject obj = (org.json.simple.JSONObject)parser.parse(deviceEventWithLandmark.toString());
                    alertingCommand = ImpoundAlert.getAlertToPublish(obj);
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }

            }

            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicDestination, alertingCommand.toString());

            System.out.println("deviceMessage = " + record.value());

            producer.send(record);
        }

        return false;
    }
}
