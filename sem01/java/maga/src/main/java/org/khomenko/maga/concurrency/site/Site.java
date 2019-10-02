package org.khomenko.maga.concurrency.site;

import java.util.Collection;
import java.util.Set;

public interface Site {
    Set<String> getPages();
    Collection<Integer> getCounters();
    Integer getPage(String page);
}
