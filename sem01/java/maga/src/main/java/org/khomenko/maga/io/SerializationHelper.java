package org.khomenko.maga.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerializationHelper<T extends Serializable> {
    private static final Logger logger = LogManager.getLogger(SerializationHelper.class);

    private Class<T> tClass;
    private ObjectMapper objectMapper;

    public SerializationHelper(Class<T> tClass) {
        this.tClass = tClass;
        this.objectMapper = new ObjectMapper();
    }

    public String readFile(String path) {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            bufferedReader.lines().forEach(line -> stringBuilder.append(line));
        }
        catch (IOException e) {
            logger.fatal(e.getMessage());
        }

        return stringBuilder.toString();
    }

    public T fromString(String json) {
        try {
            return objectMapper.readValue(json, tClass);
        }
        catch (IOException e) {
            logger.fatal(e.getMessage());
        }
        return null;
    }

    public T readFromFile(String path) {
        File file = new File(path);
        try {
            return objectMapper.readValue(file, tClass);
        }
        catch (IOException e) {
            logger.fatal(e.getMessage());
        }

        return null;
    }

    public boolean saveToFile(String path, T object) {
        File file = new File(path);
        try {
            Path parentDirPath = file.getParentFile().toPath();
            if (!Files.exists(parentDirPath)) {
                Files.createDirectories(parentDirPath);
            }

            objectMapper.writeValue(file, object);
        }
        catch (IOException e) {
            logger.fatal(e.getMessage());
            return false;
        }

        return true;
    }
}
