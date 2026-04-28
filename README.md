# lbg-case-mgmnt-service

## Build and Run 
mvn clean install
java -jar lbg-case-mgmnt-service-0.0.1-SNAPSHOT.jar

## h2 console
http://localhost:8082/h2-console
JDBS URL: jdbc:h2:mem:case_db
sa/password

## Swagger
http://localhost:8082/swagger-ui/index.html#/

## Actuator
http://localhost:8082/actuator/health


## curl cmds
curl -X 'GET' \
'http://localhost:8082/api/cases?status=OPEN&priority=HIGH&page=0&size=20' \
-H 'accept: */*'

curl -X 'GET' \
'http://localhost:8082/api/cases/CASE-2024-0001' \
-H 'accept: */*'


curl -X 'POST' \
'http://localhost:8082/api/cases' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{
"linkedAlertIds": [
"ALT-00001",
"ALT-00020"
],
"customerId": "CUST-1042",
"priority": "HIGH",
"assignedAnalyst": "j.rahman"
}'


curl -X 'POST' \
'http://localhost:8082/api/cases/CASE-2024-0003/sar' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{
"decision": "NO_ACTION",
"rationale": "Case opened and linked to alerts ALT-00001 and ALT-00020"
}'


curl -X 'POST' \
'http://localhost:8082/api/cases/CASE-2024-0003/notes' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{
"text": "CHeck completed",
"author": "ravi"
}'
  