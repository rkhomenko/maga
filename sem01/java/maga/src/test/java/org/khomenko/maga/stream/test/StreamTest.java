package org.khomenko.maga.stream.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.khomenko.maga.csv.CsvReader;
import org.khomenko.maga.stream.Iris;
import org.khomenko.maga.stream.IrisDataSetHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

class StreamTest {
    private static final double DELTA = 1e-10;
    private static IrisDataSetHelper irisDataSetHelper;

    private static Double getPetalSquare(Iris iris) {
        return iris.getPetalWidth() * iris.getPetalLength();
    }

    @BeforeAll
    static void loadDataSet() {
        var inputStream = StreamTest.class.getClassLoader().getResourceAsStream("iris_shuffle.data");
        var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            irisDataSetHelper = new IrisDataSetHelper(
                    CsvReader.readToObjects(Iris.class, bufferedReader));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAverageSepalWidth() {
        double target = 3.054;
        var value = irisDataSetHelper.getAverage(Iris::getSepalWidth);
        Assertions.assertEquals(target, value, DELTA);
    }

    @Test
    void getAveragePetalSquare() {
        double target = 5.793133333333332;
        var value = irisDataSetHelper.getAverage(StreamTest::getPetalSquare);
        Assertions.assertEquals(target, value, DELTA);
    }

    @Test
    void getAveragePetalForFlowersSepalWidthGreaterThenFour() {
        double target = 0.3433333333333333;
        var value = irisDataSetHelper.getAverageWithFilter(iris -> iris.getSepalWidth() > 4.0,
                StreamTest::getPetalSquare);
        Assertions.assertEquals(target, value, DELTA);
    }

    @Test
    void getFlowersGroupedByPetalSize() {
        var target = Arrays.asList(50, 13, 87);
        var value = new ArrayList<Integer>();

        var grouped = irisDataSetHelper.groupBy(Iris::classifyByPatel);
        for (var entry : grouped.entrySet()) {
            value.add(entry.getValue().size());
        }

        Assertions.assertTrue(target.containsAll(value));
    }

    @Test
    void getMaxSepalWidthForFlowersGroupedBySpecies() {
        var target = Arrays.asList(4.4, 3.4, 3.8);
        var value = new ArrayList<Double>();

        var grouped = irisDataSetHelper.maxFromGroupedBy(Iris::getSpecies, Iris::getSepalWidth);
        for (var entry : grouped.entrySet()) {
            value.add(entry.getValue().get().getSepalWidth());
        }

        Assertions.assertTrue(target.containsAll(value));
    }
}
