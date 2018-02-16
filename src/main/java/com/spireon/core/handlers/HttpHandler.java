package com.spireon.core.handlers;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HttpHandler {

    private String glServiceUrl;
    private String appToken;

    public HttpHandler(String glServiceUrl,
                       String identityServiceUrl,
                       String username,
                       String password,
                       String token) {
        this.glServiceUrl = glServiceUrl;

        System.out.println(identityServiceUrl);
        System.out.println(username);
        System.out.println(password);
        System.out.println(token);

        appToken = getToken(identityServiceUrl, username, password, token);
    }

    /**
     * Check if location is within landmark
     * @param deviceId
     * @param accountId
     * @param lat
     * @param lng
     * @param deviceMessage
     * @return
     */
    public JSONObject checkLocation(Long deviceId, Long accountId, double lat, double lng, JSONObject deviceMessage) {
        JSONObject message = null;

        JSONObject landmarkResponse = callGLService(glServiceUrl, appToken, deviceId, accountId, lat, lng);
        System.out.println(landmarkResponse);

        if(landmarkResponse != null) {
            JSONArray landmarks = (JSONArray)landmarkResponse.get("content");
            try {
                message = populateDeviceMessage(deviceMessage, landmarks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return message;
    }

    /**
     * Make a call to GL Service
     * @param apiUrl
     * @param token
     * @param deviceId
     * @param accountId
     * @param lat
     * @param lng
     * @return
     */
    private JSONObject callGLService(String apiUrl, String token , long deviceId , long accountId , double lat, double lng ) {
        JsonNode jNode = null;

        try {

            com.mashape.unirest.http.HttpResponse<JsonNode> response = null;

            try {
                response = Unirest.get(apiUrl)
                        .queryString("lat", lat)
                        .queryString("lng", lng)
                        .queryString("deviceId", deviceId)
                        .queryString("accountId", accountId)
                        .header("accept", "application/json")
                        .header("X-Nspire-AppToken", token)
                        .asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            jNode = response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jNode.getObject();
    }

    /**
     * Get token from Identity Service
     * @param identityServiceUrl
     * @param username
     * @param password
     * @param token
     * @return
     */
    private String getToken(String identityServiceUrl, String username, String password, String token) {

        com.mashape.unirest.http.HttpResponse<JsonNode> response = null;

        try {
            response = Unirest.get(identityServiceUrl)
                       .header("X-Nspire-AppToken", token)
                       .basicAuth(username, password).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JsonNode jNode = response.getBody();
        JSONObject obj = jNode.getObject();

        return obj.get("token").toString();
    }

    /**
     * Create a device message
     * @param deviceMessage
     * @param landmarks
     */
    private JSONObject populateDeviceMessage(JSONObject deviceMessage, JSONArray landmarks) throws Exception {
        deviceMessage.put("globalLandmarks", landmarks);

        JSONObject deviceLocation = deviceMessage.getJSONObject("location");

        if(landmarks != null && landmarks.length() > 0 ) {
            org.json.simple.JSONObject device = new org.json.simple.JSONObject();
            device.put("id",deviceMessage.get("deviceId"));
            device.put("name",null);
            device.put("description", null);
            device.put("active", true);
            device.put("serialNumber", null);
            device.put("dateCreated", formatDateInRequiredFormat((String)deviceMessage.get("eventDate")));
            device.put("lastUpdated", formatDateInRequiredFormat((String)deviceMessage.get("eventDate")));
            device.put("createdBy", null);
            device.put("updatedBy", null);
            device.put("carrierState", null);

            deviceMessage.put("device", device);
            deviceMessage.put("parseTime", formatDateInRequiredFormat((String)deviceMessage.get("eventDate")));

            org.json.simple.JSONObject deviceEvents = new org.json.simple.JSONObject();
            //creating device events
            deviceEvents.put("lat", deviceLocation.get("lat"));
            deviceEvents.put("eventDate", formatDateInRequiredFormat((String)deviceMessage.get("eventDate")));
            deviceEvents.put("cargoLoaded", null);
            deviceEvents.put("id", deviceMessage.get("eventId"));
            deviceEvents.put("geofenceIndex", null);
            deviceEvents.put("landmarkGroupId", null);
            deviceEvents.put("zip", null);
            deviceEvents.put("input4On", null);
            deviceEvents.put("harshBreaking", null);
            deviceEvents.put("landmarkId", null);
            deviceEvents.put("movingOn", null);
            deviceEvents.put("assetDistanceSum", null);
            deviceEvents.put("temp1", null);
            deviceEvents.put("diagTroubleCodes", null);
            deviceEvents.put("rssi", deviceMessage.get("rssi"));
            deviceEvents.put("stop", null);
            deviceEvents.put("extVolts", 13.649);
            deviceEvents.put("harshAccel", null);
            deviceEvents.put("speedLimit", 55);
            deviceEvents.put("state", null);
            deviceEvents.put("totalCost", null);
            deviceEvents.put("heading",deviceMessage.get("heading"));
            deviceEvents.put("lowInternalBattery", null);
            deviceEvents.put("speed", null);
            deviceEvents.put("deviceId",deviceMessage.get("deviceId"));
            deviceEvents.put("input1On", null);
            deviceEvents.put("satellites", deviceMessage.get("satelliteCount"));
            deviceEvents.put("address", null);
            deviceEvents.put("powerConnected", null);
            deviceEvents.put("powerConnectedStart", null);
            deviceEvents.put("eventIndex", null);
            deviceEvents.put("movingOnStart", null);
            deviceEvents.put("landmarkName", null);
            deviceEvents.put("input2On", null);
            deviceEvents.put("city", null);
            deviceEvents.put("setFields", null);
            deviceEvents.put("lowInternalBatteryStart", null);
            deviceEvents.put("slEst", null);
            deviceEvents.put("landmarkGlobalId", null);
            deviceEvents.put("input3On", null);
            deviceEvents.put("eventTypeCode", "MOVE_PER");
            deviceEvents.put("landmarkOn", null);
            deviceEvents.put("cargoLoadedStart", null);
            deviceEvents.put("odometer", 0);
            deviceEvents.put("stopStart", null);
            deviceEvents.put("intVolts", 8.208);
            deviceEvents.put("lng", deviceLocation.get("lng"));
            deviceEvents.put("eventTypeName", "Movement");


            deviceMessage.put("deviceEvent",deviceEvents);
        }

        return deviceMessage;
    }

    /**
     * Format the date
     * @param parseDate
     * @return
     * @throws Exception
     */
    private String formatDateInRequiredFormat(String parseDate) throws Exception {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = originalFormat.parse(parseDate);
        String formattedDate = targetFormat.format(date);
        return formattedDate;
    }
}
