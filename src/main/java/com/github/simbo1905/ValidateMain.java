package com.github.simbo1905;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import com.beust.jcommander.ParameterException;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

enum SchemaType {
    JSON,
    AVRO,
    PROTOBUF;

    public static SchemaType fromString(String code) {
        for(SchemaType output : SchemaType.values()) {
            if(output.toString().equalsIgnoreCase(code)) {
                return output;
            }
        }
        return null;
    }
}

class OutputConverter implements IStringConverter<SchemaType> {
    @Override
    public SchemaType convert(String value) {
        SchemaType convertedValue = SchemaType.fromString(value);

        if(convertedValue == null) {
            throw new ParameterException("Value " + value + "can not be converted to OutputEnum. " +
                    "Available values are: json, avro, protobuf.");
        }
        return convertedValue;
    }
}

public class ValidateMain {
    private static Logger logger = LoggerFactory.getLogger(ValidateMain.class);

    public static class Args {
        @Parameter(names = { "--help", "-h" }, help = true)
        private boolean help = false;

        public boolean isHelp() {
            return help;
        }

        @Parameter(
                names = {"-r", "--read-schema"},
                description = "read schema file",
                required = true
        )
        String readSchema;

        @Parameter(
                names = {"-w", "--write-schema"},
                description = "write schema file",
                required = true
        )
        String writeSchema;

        @Parameter(names = {"-t", "--type"}, description = "Schema type: json, avro or protobuf", required = true, converter = OutputConverter.class)
        SchemaType type;

        @Override
        public String toString() {
            return "Args{" +
                    "readSchema='" + readSchema + '\'' +
                    ", writeSchema='" + writeSchema + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    public static void main(String[] arguments) throws IOException {
        Args args = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(args)
                .build();
        commander.parse(arguments);

        if( args.isHelp() ) {
            commander.usage();
            return;
        }

        logger.info(args.toString());

        final var readPath = Paths.get(args.readSchema);
        if( readPath.toFile().exists() == false || readPath.toFile().isFile() == false ){
            logger.error("is not a file: "+args.readSchema);
        }
        final var readSchemaString = Files.readAllLines(readPath).stream().collect(Collectors.joining());

        final var writePath = Paths.get(args.writeSchema);
        if( writePath.toFile().exists() == false || writePath.toFile().isFile() == false ){
            logger.error("is not a file: "+args.writeSchema);
        }
        final var writeSchemaString = Files.readAllLines(writePath).stream().collect(Collectors.joining());

        final var validator = new Validator(args.type);

        final var jsonSchemaRead = validator.parse(readSchemaString);
        final var jsonSchemaWrite = validator.parse(writeSchemaString);

        final var errors = validator.validate(jsonSchemaRead, jsonSchemaWrite);
        for( var e : errors) {
            logger.error(e);
        }
        System.exit(errors.size());
    }
}
