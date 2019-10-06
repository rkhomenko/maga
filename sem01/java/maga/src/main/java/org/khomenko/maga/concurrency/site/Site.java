package org.khomenko.maga.concurrency.site;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Site {
    private Set<String> pages;
    private Collection<Integer> results;

    public Site() {
        pages = IntStream.rangeClosed(1, 5).mapToObj(i -> "/page?id=" + i).collect(Collectors.toSet());
    }

    protected void setResults(Collection<Integer> results) {
        this.results = results;
    }

    protected void onGetCounters() {}

    public Set<String> getPages() {
        return pages;
    }

    public Collection<Integer> getCounters() {
        onGetCounters();
        return results;
    }

    public abstract Integer getPage(String page);
}
