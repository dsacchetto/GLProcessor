How To run it
==================

java -jar /dir/to/file/GLProcessor-1.0-SNAPSHOT-jar-with-dependencies.jar kafkaHost kafkaPort topicSource topicDestination groupId debugOn

#### Default Values
Prod environment

```
kafkaHost: "kafka.i.spireon.com"
kafkaPort: "9092"
topicSource: "DeviceLocationChangeTopic"
topicDestination: "DeviceLocationChangeTopicImpound"
groupId: "ImpoundLotGroupId"
debugOn: false
```
