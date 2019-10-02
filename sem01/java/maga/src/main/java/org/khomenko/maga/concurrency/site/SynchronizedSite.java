package org.khomenko.maga.concurrency.site;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class SynchronizedSite implements Site {
    private Map<String, Integer> requestCounter;

    public SynchronizedSite() {
        requestCounter = new HashMap<>();
        IntStream.rangeClosed(1, 5).forEach(i -> requestCounter.put("/page?id=" + i, 0));
    }

    public Set<String> getPages() {
        return requestCounter.keySet();
    }

    public Collection<Integer> getCounters() {
        return requestCounter.values();
    }

    public Integer getPage(String page) {
        synchronized (requestCounter) {
            if (!requestCounter.containsKey(page)) {
                throw new UnsupportedOperationException();
            }
            return requestCounter.merge(page, 1, Integer::sum);
        }
    }
}
