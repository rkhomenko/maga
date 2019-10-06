package org.khomenko.maga.concurrency.benchmark.site;

import org.khomenko.maga.concurrency.site.AtomicSite;
import org.khomenko.maga.concurrency.site.Site;
import org.khomenko.maga.concurrency.site.SynchronizedSite;
import org.khomenko.maga.concurrency.site.ThreadRunner;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G", "--enable-preview"})
public class SiteBenchmark {
    public static void main(String[] args)  throws RunnerException {
        Options options = new OptionsBuilder()
                .include(SiteBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(options).run();
    }

    private Map<String, Site> sites;

    private void doSiteBenchmark(Site site) {
        ThreadRunner threadRunner = new ThreadRunner(site);
        threadRunner.run();
    }

    @Setup
    public void setup() {
        sites = new HashMap<>();
        sites.put("SyncSite", new SynchronizedSite());
        sites.put("AtomicSite", new AtomicSite());
    }

    @Param({"SyncSite", "AtomicSite"})
    public String siteTag;

    @Benchmark
    public void doBenchmark() {
        Site site = sites.get(siteTag);
        doSiteBenchmark(site);
    }
}

