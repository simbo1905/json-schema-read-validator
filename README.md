# Json Read Schema Validator

A simple util based on Confluent Kafka Client that will validate if a given read schema is compatible
with a specific write schema when both are locally on disk. This is **not** the same as asking an API whether 
a new proposed schema is compatible or transitively compatible with a schema. Rather it allows yo to check 
whether a possibly out of date reader able to read a given writer schema. See [this SO question](https://stackoverflow.com/q/74798503/329496)
for a discussion. 

## Usage

```shell
# This fails as missing required fields
java com.github.simbo1905.ValidateMain -r schemaB.json -w schemaA.json
```
