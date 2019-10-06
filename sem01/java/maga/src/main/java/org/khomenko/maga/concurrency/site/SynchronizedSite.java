package org.khomenko.maga.concurrency.site;

import java.util.HashMap;
import java.util.Map;

public class SynchronizedSite extends Site {
    private final Map<String, Integer> requestCounter;

    public SynchronizedSite() {
        requestCounter = new HashMap<>();
        getPages().forEach(page -> requestCounter.put(page, 0));
    }

    @Override
    protected void onGetCounters() {
        setResults(requestCounter.values());
    }

    @Override
    public Integer getPage(String page) {
        synchronized (requestCounter) {
            if (!requestCounter.containsKey(page)) {
                throw new UnsupportedOperationException();
            }
            return requestCounter.merge(page, 1, Integer::sum);
        }
    }
}
