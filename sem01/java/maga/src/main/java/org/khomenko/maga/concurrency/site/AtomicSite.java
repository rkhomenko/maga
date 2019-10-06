package org.khomenko.maga.concurrency.site;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AtomicSite extends Site {
    private Map<String, AtomicInteger> requestCounter;

    public AtomicSite() {
        requestCounter = new HashMap<>();
        getPages().forEach(page -> requestCounter.put(page, new AtomicInteger(0)));
    }

    @Override
    protected void onGetCounters() {
        setResults(requestCounter.values().stream()
                .map(AtomicInteger::get)
                .collect(Collectors.toList()));
    }

    @Override
    public Integer getPage(String page) {
        return requestCounter.get(page).incrementAndGet();
    }
}
