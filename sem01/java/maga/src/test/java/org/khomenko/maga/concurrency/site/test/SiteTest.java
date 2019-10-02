package org.khomenko.maga.concurrency.site.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.khomenko.maga.concurrency.site.SynchronizedSite;
import org.khomenko.maga.concurrency.site.Site;
import org.khomenko.maga.concurrency.site.ThreadRunner;

class SiteTest {
    @Test
    void objectSynchronizedSiteTest() {
        Site site = new SynchronizedSite();
        ThreadRunner threadRunner = new ThreadRunner(site);

        threadRunner.run();
        Assertions.assertEquals(threadRunner.expectedSum(), threadRunner.sum());
    }
}
