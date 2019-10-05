package org.khomenko.maga.concurrency.du;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ComputeRecursiveTask extends RecursiveTask<Long> {
    private Collection<Path> directories;

    ComputeRecursiveTask(Collection<Path> directories) {
        this.directories = directories;
    }

    @Override
    protected Long compute() {
        Supplier<Stream<Path>> supplier = () ->
                directories.stream()
                        .flatMap(path -> {
                            try {
                                return Files.walk(path, 1).skip(1);
                            } catch (IOException e) {
                                System.out.printf("Skip directory %s\n", path);
                            }
                            return Stream.empty();
                        });

        var bytesCount = supplier.get()
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        System.out.printf("Skip file %s: %s", path, e.getMessage());
                    }
                    return 0;
                })
                .sum();

        Collection<Path> subDirectories = supplier.get()
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        if (subDirectories.size() == 0) {
            return bytesCount;
        }

        var subtasks = createSubtasks(subDirectories);
        subtasks.forEach(RecursiveTask::fork);

        var subtaskBytesCount = subtasks.stream()
                .mapToLong(RecursiveTask::join)
                .sum();

        return bytesCount + subtaskBytesCount;
    }

    private Collection<ComputeRecursiveTask> createSubtasks(Collection<Path> directories) {
        var subtasks = new ArrayList<ComputeRecursiveTask>();

        var second = directories.spliterator();
        var first = second.trySplit();

        if (first != null) {
            subtasks.add(new ComputeRecursiveTask(StreamSupport.stream(first, false)
                    .collect(Collectors.toList())));
        }

        subtasks.add(new ComputeRecursiveTask(StreamSupport.stream(second, false)
                .collect(Collectors.toList())));

        return subtasks;
    }
}
