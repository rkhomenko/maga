package org.khomenko.maga.concurrency.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

public class ThreadRunner {
    private static final int threadNum = 50;
    private static final int tryNum = 50;

    private Site site;

    public ThreadRunner(Site site) {
        this.site = site;
    }

    public void run() {
        Runnable task = () -> {
            var pages = new ArrayList<>(site.getPages());
            Collections.shuffle(pages);

            for (int i = 0; i < tryNum; i++) {
                for (var page : pages) {
                    site.getPage(page);
                }
            }
        };
        ExecutorService service = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            service.submit(task);
        }

        service.shutdown();
    }

    public Integer expectedSum() {
        return site.getPages().size() * threadNum * tryNum;
    }

    public Integer sum() {
        return site.getCounters().stream().reduce(0, Integer::sum);
    }
}
