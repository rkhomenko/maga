package org.khomenko.maga.concurrency.du.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.khomenko.maga.concurrency.du.DiskUsageComputer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class DiskUsageTest {
    private static final String directory = "/opt";
    private static long size;

    @BeforeAll
    static void getDirectorySize() {
        try {
            size = Files.walk(Path.of(directory))
                    .parallel()
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                        }
                        return 0;
                    })
                    .sum();
        }
        catch (IOException e) {
        }
    }

    @Test
    void test() {
        var diskUsageComputer = new DiskUsageComputer("/opt", 30);
        Assertions.assertEquals(size, diskUsageComputer.compute());
    }
}
