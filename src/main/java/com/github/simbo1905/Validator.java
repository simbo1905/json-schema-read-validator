package com.github.simbo1905;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.SchemaValidator;
import io.confluent.kafka.schemaregistry.SchemaValidatorBuilder;

import java.util.Collections;
import java.util.List;

public class Validator {
    SchemaValidator validator = new SchemaValidatorBuilder().canBeReadStrategy().validateLatest();
    public List<String> validate(ParsedSchema reader, ParsedSchema writer) {
        return validator.validate(reader, Collections.singleton(writer));
    }
}
