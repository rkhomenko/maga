package org.khomenko.maga.concurrency.site;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class Site implements ISite {
    private Map<String, Integer> requestCounter;

    public Site() {
        requestCounter = new HashMap<>();
        IntStream.rangeClosed(1, 5).forEach(i -> requestCounter.put("/page?id=" + String.valueOf(i), 0));
    }

    public Set<String> getPages() {
        return requestCounter.keySet();
    }

    public Collection<Integer> getCounters() {
        return requestCounter.values();
    }

    public Integer getPage(String page) {
        if (!requestCounter.containsKey(page)) {
            throw new UnsupportedOperationException();
        }

        Integer counter = requestCounter.get(page);
        synchronized (counter) {
            counter += 1;
        }

        return counter;
    }
}
