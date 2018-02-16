package com.spireon.core.handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImpoundAlert {
    public static JSONObject getAlertToPublish(JSONObject deviceEventWithMessage) throws Exception {

        JSONObject eventAlertType = new JSONObject();
        eventAlertType.put("name", "GlobalLandmarkAlert");
        eventAlertType.put("id", 1234l);


        JSONObject alertObject = new JSONObject();
        alertObject.put("geofenceEvents", null);
        alertObject.put("deviceEvent", null);
        alertObject.put("deviceId",(Long)deviceEventWithMessage.get("deviceId"));
        alertObject.put("landmarkExitList", null);
        alertObject.put("alerterService", null);
        alertObject.put("alertType", null);
        alertObject.put("correlation", "3681340d-18bc-4f5a-8ca3-62f7090aed3d");
        alertObject.put("lastState", new JSONObject());
        alertObject.put("landmarkEnterList", null);
        alertObject.put("checkDate", null);
        alertObject.put("alertSpec", null);
        alertObject.put("stateId", null);
        alertObject.put("landmarkList", null);
        alertObject.put("eventAlertType", eventAlertType);
        alertObject.put("alertList", null);
        alertObject.put("landmarkChangeList", null);
        alertObject.put("accountId",(Long) deviceEventWithMessage.get("accountId"));
        alertObject.put("parseTime",formatDateInRequiredFormat((String)deviceEventWithMessage.get("eventDate")));
        alertObject.put("alertSpec", null);
        alertObject.put("device", deviceEventWithMessage.get("device"));
        alertObject.put("eventDate", formatDateInRequiredFormat((String)deviceEventWithMessage.get("eventDate")));
        alertObject.put("fixDate", formatDateInRequiredFormat((String)deviceEventWithMessage.get("eventDate")));

        JSONArray landmarks = (JSONArray) deviceEventWithMessage.get("globalLandmarks");

        JSONArray landmarkListArray = new JSONArray();

        int count = 0;
        String landmarkName =  null;
        String landmarkAddress = null;
        String landmarkGlobalId = null;
        String city = null;
        String zipcode = null;
        String state = null;
        String lat = null;
        String lng = null;

        if(landmarks != null && landmarks.size() > 0) {
            for (int i = 0; i < landmarks.size(); i++) {
                JSONObject landmark = (JSONObject) landmarks.get(i);

                JSONObject glLandmark = new JSONObject();
                glLandmark.put("landmarkId", (long) i + 1);
                glLandmark.put("landmarkGlobalId", (String) landmark.get("globalId"));
                glLandmark.put("deviceCount", 1);
                glLandmark.put("endDate", formatDateInRequiredFormat((String)deviceEventWithMessage.get("eventDate")));
                glLandmark.put("startDate", formatDateInRequiredFormat((String)deviceEventWithMessage.get("eventDate")));
                glLandmark.put("name", (String) landmark.get("name"));
                glLandmark.put("landmarkName", (String) landmark.get("name"));
                glLandmark.put("landmarkType", "globalLandmarkVisit");
                glLandmark.put("parAny", 1);
                landmarkListArray.add(count, glLandmark);

                landmarkName = (String) landmark.get("name");
                landmarkAddress = (String) landmark.get("address");
                landmarkGlobalId = (String) landmark.get("globalId");
                city = (String) landmark.get("city");
                zipcode = (String) landmark.get("zip");
                state = (String) landmark.get("state");

                Object latObj = landmark.get("lat");
                Object longObj = landmark.get("lng");

                lat = latObj instanceof Double ? Double.toString((Double)latObj) : (String)latObj;
                lng = longObj instanceof Double ? Double.toString((Double)longObj) : (String)longObj;

                count++;
            }
        }

        JSONObject deviceEvent = new JSONObject();
        deviceEvent.put("eventDate",formatDateInRequiredFormat((String)deviceEventWithMessage.get("eventDate")));

        JSONObject address = new JSONObject();
        address.put("address", landmarkAddress);
        address.put("city", city);
        address.put("state", state);
        address.put("zip", zipcode);

        alertObject.put("newState", address);

        alertObject.put("deviceEvent", buildDeviceEvent(deviceEvent,
                                                        (Long)deviceEventWithMessage.get("deviceId"),
                                                         landmarkName,
                                                         landmarkAddress, landmarkGlobalId, city, state,zipcode, lat, lng));

        alertObject.put("landmarkList", landmarkListArray);

        return alertObject;
    }

    private static JSONObject buildDeviceEvent(JSONObject deviceEvent,
                                               Long deviceId,
                                               String landmarkName,
                                               String address,
                                               String globalId,
                                               String city,
                                               String state,
                                               String zip,
                                               String lat,
                                               String lng) {
        deviceEvent.put("deviceId",deviceId);
        deviceEvent.put("landmarkId",0);
        deviceEvent.put("landmarkGlobalId",globalId);
        deviceEvent.put("landmarkName",landmarkName);
        deviceEvent.put("landmarkGroupId",null);
        deviceEvent.put("eventTypeCode",null);
        deviceEvent.put("eventTypeName",null);
        deviceEvent.put("assetDistanceSum",0);
        deviceEvent.put("totalCost",0.00);
        deviceEvent.put("intVolts",0.00);
        deviceEvent.put("extVolts",0.00);
        deviceEvent.put("speed",0.00);
        deviceEvent.put("speedLimit",0);
        deviceEvent.put("stop",true);
        deviceEvent.put("stopStart",0);
        deviceEvent.put("lowInternalBattery",false);
        deviceEvent.put("lowInternalBatteryStart",0l);
        deviceEvent.put("powerConnected",true);
        deviceEvent.put("powerConnectedStart",0l);
        deviceEvent.put("cargoLoaded",false);
        deviceEvent.put("cargoLoadedStart",0);
        deviceEvent.put("setFields",null);
        deviceEvent.put("input1On",false);
        deviceEvent.put("input2On",false);
        deviceEvent.put("input3On",false);
        deviceEvent.put("input4On",false);
        deviceEvent.put("eventIndex",0);
        deviceEvent.put("movingOn",false);
        deviceEvent.put("movingOnStart",0);
        deviceEvent.put("harshBreaking",false);
        deviceEvent.put("harshAccel",false);
        deviceEvent.put("diagTroubleCodes",null);
        deviceEvent.put("geofenceIndex",0);
        deviceEvent.put("temp1",0.00);
        deviceEvent.put("slEst",false);
        deviceEvent.put("lat",lat);
        deviceEvent.put("lng",lng);
        deviceEvent.put("odometer",0.00);
        deviceEvent.put("heading",0.00);
        deviceEvent.put("address",address);
        deviceEvent.put("city",city);
        deviceEvent.put("state",state);
        deviceEvent.put("zip",zip);
        deviceEvent.put("rssi",0.00);
        deviceEvent.put("satellites",0);
        deviceEvent.put("landmarkOn",false);
        return deviceEvent;
    }

    private static String formatDateInRequiredFormat(String parseDate) throws Exception {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = originalFormat.parse(parseDate);
        String formattedDate = targetFormat.format(date);
        return formattedDate;
    }
}