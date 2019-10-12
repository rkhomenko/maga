package org.khomenko.maga.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CsvReader {
    public static <T> Collection<T> readToObjects(Class<T> c, BufferedReader reader) throws IOException {
        var columnNames = parseLine(reader.readLine());
        var columnToField = generateColumnToFiledName(c, new HashSet<>(Arrays.asList(columnNames)));
        var result = new ArrayList<T>();

        var line = reader.readLine();
        while (line != null) {
            var columnValues = parseLine(line);
            T object = null;
            try {
                object = c.getDeclaredConstructor().newInstance();
            }
            catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }

            for (int i = 0; i < columnValues.length; i++) {
                setObjectField(object, columnToField.get(columnNames[i]), columnValues[i]);
            }
            result.add(object);

            line = reader.readLine();
        }
        reader.close();

        return result;
    }

    private static String[] parseLine(String line) {
        return line.split(",");
    }

    private static Map<String, String> generateColumnToFiledName(Class<?> c, Set<String> columnNames) {
        String defaultColumnName = null;
        try {
            defaultColumnName = (String)CsvColumn.class.getDeclaredMethod("name").getDefaultValue();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        var columnToField = new HashMap<String, String>();

        for (var field : c.getDeclaredFields()) {
            var annotation = field.getAnnotation(CsvColumn.class);
            if (annotation != null) {
                if (!annotation.name().equals(defaultColumnName)) {
                    if (!columnNames.contains(annotation.name())) {
                        throw new CsvReaderBadAnnotationException(
                                "Column name " + annotation + " doesn't contain in CSV");
                    }

                    columnToField.put(annotation.name(), field.getName());
                }
                else {
                    if (columnNames.contains(field.getName())) {
                        columnToField.put(field.getName(), field.getName());
                    }
                    else {
                        throw new CsvReaderBadAnnotationException(
                                "Column name " + field.getName() + " doesn't contain in CSV");
                    }
                }
            }
        }

        if (columnNames.size() != columnToField.size()) {
            throw new CsvReaderBadAnnotationException("All columns must be annotated");
        }

        return columnToField;
    }

    private static void setObjectField(Object obj, String objectField, String value) {
        try {
            var field = obj.getClass().getDeclaredField(objectField);
            field.setAccessible(true);
            field.set(obj, parseString(field.getType(), value));
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Object parseString(Class<?> c, String value) {
        if (Boolean.class == c || Boolean.TYPE == c) {
            return Boolean.parseBoolean(value);
        }

        if (Byte.class == c || Byte.TYPE == c) {
            return Byte.parseByte(value);
        }

        if (Short.class == c || Short.TYPE == c) {
            return Short.parseShort(value);
        }

        if (Integer.class == c || Integer.TYPE == c) {
            return Integer.parseInt(value);
        }

        if (Long.class == c || Long.TYPE == c) {
            return Long.parseLong(value);
        }

        if (Float.class == c || Float.TYPE == c) {
            return Float.parseFloat(value);
        }

        if (Double.class == c || Double.TYPE == c) {
            return Double.parseDouble(value);
        }

        if (String.class == c) {
            return value;
        }

        throw new RuntimeException("Cannot convert string to class " +  c.getName());
    }
}
