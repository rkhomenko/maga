package org.khomenko.maga.concurrency.du;

import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class DiskUsageComputer {
    private Path path;
    private int threadCount;

    public DiskUsageComputer(String path, int threadCount) {
        this.path = Path.of(path);
        this.threadCount = threadCount;
    }

    public Long compute() {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        ComputeRecursiveTask computeRecursiveTask = new ComputeRecursiveTask(Collections.singletonList(path));

        return forkJoinPool.invoke(computeRecursiveTask);
    }
}
