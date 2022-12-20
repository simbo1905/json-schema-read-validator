package com.github.simbo1905;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import io.confluent.kafka.schemaregistry.json.JsonSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;


public class ValidateMain {
    private static Logger logger = LoggerFactory.getLogger(ValidateMain.class);

    public static class Args {
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

        @Override
        public String toString() {
            return "Args{" +
                    "readSchema='" + readSchema + '\'' +
                    ", writeSchema='" + writeSchema + '\'' +
                    '}';
        }
    }



    public static void main(String[] arguments) throws IOException {
        Args args = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(args)
                .build();
        commander.parse(arguments);
        logger.info(args.toString());

        final var readPath = Paths.get(args.readSchema);
        if( readPath.toFile().exists() == false || readPath.toFile().isFile() == false ){
            logger.error("is not a file: "+args.readSchema);
        }
        final var readSchemaString = Files.readAllLines(readPath).stream().collect(Collectors.joining());
        final var jsonSchemaRead = new JsonSchema(readSchemaString);

        final var writePath = Paths.get(args.writeSchema);
        if( writePath.toFile().exists() == false || writePath.toFile().isFile() == false ){
            logger.error("is not a file: "+args.writeSchema);
        }
        final var writeSchemaString = Files.readAllLines(writePath).stream().collect(Collectors.joining());
        final var jsonSchemaWrite = new JsonSchema(writeSchemaString);


        final var validator = new Validator();
        final var errors = validator.validate(jsonSchemaRead, jsonSchemaWrite);
        for( var e : errors) {
            logger.error(e);
        }
        System.exit(errors.size());
    }
}
