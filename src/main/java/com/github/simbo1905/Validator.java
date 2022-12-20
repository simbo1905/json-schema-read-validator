package com.github.simbo1905;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.SchemaValidator;
import io.confluent.kafka.schemaregistry.SchemaValidatorBuilder;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import io.confluent.kafka.schemaregistry.protobuf.ProtobufSchema;

import java.util.Collections;
import java.util.List;

public class Validator {
    final SchemaValidator validator = new SchemaValidatorBuilder().canBeReadStrategy().validateLatest();
    final SchemaType type;

    public Validator(SchemaType type) {
        this.type = type;
    }

    public ParsedSchema parse( final String schemaString ) {
        if( this.type == SchemaType.JSON ){
            return new JsonSchema(schemaString);
        } else if (this.type == SchemaType.AVRO ) {
            return new AvroSchema(schemaString);
        } else if (this.type == SchemaType.PROTOBUF) {
            return new ProtobufSchema(schemaString);
        } else {
            throw new IllegalArgumentException("type should be a known value but got "+this.type);
        }
    }

    public List<String> validate(ParsedSchema reader, ParsedSchema writer) {
        return validator.validate(reader, Collections.singleton(writer));
    }
}
