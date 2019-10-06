package org.khomenko.maga.concurrency.site;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncWrapperSite extends Site{
    private final Map<String, Integer> requestCounter;

    public SyncWrapperSite() {
        requestCounter = Collections.synchronizedMap(new HashMap<>());
        getPages().forEach(page -> requestCounter.put(page, 0));
    }

    @Override
    protected void onGetCounters() {
        setResults(requestCounter.values());
    }

    @Override
    public Integer getPage(String page) {
        if (!requestCounter.containsKey(page)) {
            throw new UnsupportedOperationException();
        }
        return requestCounter.merge(page, 1, Integer::sum);
    }
}