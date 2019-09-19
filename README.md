# Device Manager

## Building application
Application is built with command
mvn package
Script build.sh executes this command
.\build.sh
## Running application
Application is run with command
mvn spring-boot:run
Script start.sh executes this command
.\start.sh
## Available REST Endpoints

### Device Registration
Example CURL command:

curl -X POST \
  http://localhost:8080/devices \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'origin: http://localhost:8080' \
  -H 'postman-token: a009a6d8-8d7b-fb6e-2088-473047a7e604' \
  -d '{
"name" : "mobile",
"secretKey" : "xxx"
}'

### Retrieval of device details
Example CURL command:

curl -X GET \
  http://localhost:8080/devices/113fc579-b4df-49e4-9528-e1d2e35fa453 \
  -H 'cache-control: no-cache' \
  -H 'postman-token: e107c6f1-6821-1267-d8fa-e5d121d00d61' \
  -H 'secretkey: xxx'
  
### Device Status Update
Example CURL command:

curl -X PATCH \
  http://localhost:8080/devices/9d01a2f3-5c44-4e37-805f-fd79f7760c50 \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: ab1013e5-9443-dcea-b6cc-8ebc95453074' \
  -d '{
"deviceStatus" : "OK",
"secretKey" : "xxx"
}'

### Listing devices by status
Example CURL command:

curl -X GET \
  'http://localhost:8080/devices?status=STALE' \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 8fc0ae40-75d1-767d-19b6-402be3ef946d'
  
###Listing all devices

curl -X GET \
  http://localhost:8080/devices \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 221ffb65-9bf4-e668-ef47-e628393cd635'
  
## Automatic device status expiration

Automatic device status expiration is handled by Delay Queue. When the Status of the Device is set to OK
the StatusExpirationDelayObject with information about DeviceId and the expiration time is put to the 
StatusExpirationDelayQueue. When the expiration time has passed the StatusExpirationDelayObject is taken from the queue.
The Device with it's deviceId is set to STALE.
