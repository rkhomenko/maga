package org.khomenko.maga.csv.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.khomenko.maga.csv.CsvReader;
import org.khomenko.maga.stream.Iris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

class CsvTest {
    @Test
    void parseAndReflectTest() {
        var inputStream = CsvTest.class.getClassLoader().getResourceAsStream("iris_test.data");
        var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Collection<Iris> parsedIrises = null;

        var targetIrises = Arrays.asList(
                new Iris(5.1,3.5,1.4,0.2,"Iris-setosa"),
                new Iris(7.0,3.2,4.7,1.4,"Iris-versicolor"));

        try {
            parsedIrises = CsvReader.readToObjects(Iris.class, bufferedReader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(targetIrises, parsedIrises);
    }
}
