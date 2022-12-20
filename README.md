# Json Read Schema Validator

A simple util based on Confluent Kafka Client that will validate if a given read schema is compatible
with a specific write schema when both are locally on disk. This is **not** the same as asking an API whether 
a new proposed schema is compatible or transitively compatible with a latest or historic published schemas. 
Rather it allows you to check whether a possibly out-of-date reader will be able to read a given writer schema. 
See [this SO question](https://stackoverflow.com/q/74798503/329496) for a discussion. 

## Usage

```shell
mvn package
# This finds issues with json
java -jar ./target/json-schema-read-validator-1.0-SNAPSHOT.jar -t json -r json_schemaB.json -w json_schemaA.json
# This finds issues with avro
java -jar ./target/json-schema-read-validator-1.0-SNAPSHOT.jar -t avro -r avro_schemaB.json -w avro_schemaA.json
```

In theory protobuf schema is available but not heavily test. 
