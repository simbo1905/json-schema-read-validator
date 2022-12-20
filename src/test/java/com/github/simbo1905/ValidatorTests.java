package com.github.simbo1905;

import org.everit.json.schema.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.kafka.schemaregistry.json.JsonSchema;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidatorTests {
    private static Logger logger = LoggerFactory.getLogger(ValidatorTests.class);
    @DisplayName("Same rich schema")
    @Test
    void testSame1() {
        Validator v = new Validator();
        Schema s = ObjectSchema.builder()
                .addPropertySchema("boolF", BooleanSchema.builder().build())
                .addRequiredProperty("boolF")
                .addPropertySchema("intF", NumberSchema.builder().requiresInteger(true).build())
                .addRequiredProperty("intF")
                .addPropertySchema("numberF", NumberSchema.builder().requiresNumber(true).build())
                .addRequiredProperty("numberF")
                .addPropertySchema("stringF", StringSchema.builder().build())
                .addRequiredProperty("stringF")
                .addPropertySchema("enumF", EnumSchema.builder().possibleValue("S").build())
                .addRequiredProperty("enumF")
                .addPropertySchema(
                        "arrayF",
                        ArraySchema.builder().allItemSchema(StringSchema.builder().build()).build()
                )
                .addRequiredProperty("arrayF")
                .addPropertySchema(
                        "recordF",
                        ObjectSchema.builder().addPropertySchema("f", NumberSchema.builder().build()).build()
                )
                .addRequiredProperty("recordF")
                .addPropertySchema("bool0", BooleanSchema.builder().build())
                .build();
        assertThat(v.validate(new JsonSchema(s), new JsonSchema(s))).isEmpty();
        logger.info("Success");
    }

    Schema abNoAdditionalNoRequired = ObjectSchema.builder()
            .addPropertySchema("a", NumberSchema.builder().requiresNumber(true).build())
            .addPropertySchema("b", NumberSchema.builder().requiresInteger(true).defaultValue(0).build())
            .additionalProperties(true)
            .build();
    Schema cdAdditionalRequiredC = ObjectSchema.builder()
            .addPropertySchema("c", NumberSchema.builder().requiresNumber(true).build())
            .addPropertySchema("d", NumberSchema.builder().requiresInteger(true).build())
            .additionalProperties(true)
            .addRequiredProperty("c")
            .build();

    @DisplayName("Reader does not require.")
    @Test
    void testDifferent1() {
        Validator v = new Validator();
        assertThat(v.validate(new JsonSchema(abNoAdditionalNoRequired), new JsonSchema(cdAdditionalRequiredC))).isNotEmpty();
    }

    @DisplayName("Reader requires writer does not provide.")
    @Test
    void testDifferent2() {
        Validator v = new Validator();
        final var r = v.validate(new JsonSchema(cdAdditionalRequiredC), new JsonSchema(abNoAdditionalNoRequired));
        assertThat(r).isNotEmpty();
    }

    @DisplayName("Reader requires writer requires same no additional properties.")
    @Test
    void testSame2() {
        Validator v = new Validator();
        final var results = v.validate(new JsonSchema(cdAdditionalRequiredC), new JsonSchema(cdAdditionalRequiredC));
        assertThat(results).isEmpty();
    }
}
