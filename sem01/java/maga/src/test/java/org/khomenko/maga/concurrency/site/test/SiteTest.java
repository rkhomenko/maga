package org.khomenko.maga.concurrency.site.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.khomenko.maga.concurrency.site.AtomicSite;
import org.khomenko.maga.concurrency.site.SynchronizedSite;
import org.khomenko.maga.concurrency.site.Site;
import org.khomenko.maga.concurrency.site.ThreadRunner;

import java.util.stream.Stream;

class SiteTest {
    static Stream<Site> sitesProvider() {
        return Stream.of(new SynchronizedSite(),
                new AtomicSite());
    }

    @ParameterizedTest
    @MethodSource("sitesProvider")
    void objectSynchronizedSiteTest(Site site) {
        ThreadRunner threadRunner = new ThreadRunner(site);
        threadRunner.run();
        Assertions.assertEquals(threadRunner.expectedSum(), threadRunner.sum());
    }
}
