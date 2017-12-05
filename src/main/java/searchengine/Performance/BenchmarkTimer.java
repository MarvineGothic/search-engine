package searchengine.Performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class gives a benchmark with average runtime, total run time and standard deviations of all run times
 */
public class BenchmarkTimer {
    private Callable<Integer> callable;
    private int warmUpIterations;
    private int iterations;
    private List<Long> runTimes = new ArrayList<>();
    private long meanRuntime;
    private long totalRuntime;
    private long stdRuntime;
    private long stdError;

    /**
     * Create a benchmark of a method.
     *
     * @param callable         An implementation of the Callable<Integer> interface. The Call() method is the function called
     *                         when benchmarking.
     * @param iterations       The number of iterations the benchmark is based on.
     * @param warmUpIterations The number of warm up iterations used for the benchmark.
     * @throws Exception If the Callable method throws an axception it needs to be caught.
     */
    public BenchmarkTimer(Callable<Integer> callable, int iterations, int warmUpIterations) throws Exception {
        this.callable = callable;
        this.warmUpIterations = warmUpIterations;
        this.iterations = iterations;
        run();
    }

    /**
     * Create a benchmark of a method. The number of iterations is set to 100 with 1 warm up iterations as default.
     *
     * @param callable An implementation of the Callable<Integer> interface. The Call() method is the function called
     *                 when benchmarking.
     * @throws Exception If the Callable method throws an axception it needs to be caught.
     */
    public BenchmarkTimer(Callable<Integer> callable) throws Exception {
        this.callable = callable;
        this.iterations = 100;
        this.warmUpIterations = 1;
        run();
    }


    /**
     * @return The averate run time when Callable is called.
     */
    public long getMeanRuntime() {
        return meanRuntime;
    }

    /**
     * @return The toal runtime of all iterations
     */
    public long getTotalRuntime() {
        return totalRuntime;
    }

    /**
     * @return The standard deviation of all calls
     */
    public long getStdRuntime() {
        return stdRuntime;
    }

    /**
     * The standard deviation of the average runtime (standard deviation divided by sqrt(iterations)
     * @return standard error on the mean.
     */
    public long getStdError() {
        return stdError;
    }

    /**
     * This method runs the actual benchmark.
     * @throws Exception If the Callable method throws an axception it needs to be caught.
     */
    private void run() throws Exception {
        // Warm up
        for (int i = 0; i < warmUpIterations; i++) {
            callable.call();
        }

        // Actual iterations
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            callable.call();
            long elapsedTime = System.nanoTime() - startTime;
            runTimes.add(elapsedTime);
        }

        // Calculate benchmark statistics
        meanRuntime = 0;
        totalRuntime = 0;
        stdRuntime = 0;
        for (float runTime : runTimes) {
            totalRuntime += runTime;
        }
        meanRuntime = totalRuntime / runTimes.size();

        for (float runTime : runTimes) {
            stdRuntime += (meanRuntime - runTime) * (meanRuntime - runTime);
        }
        stdRuntime = (long) Math.sqrt((double) (stdRuntime / (runTimes.size() - 1)));
        stdError = (long) (stdRuntime / Math.sqrt(runTimes.size() - 1));
    }

    @Override
    public String toString() {
        return String.format("BenchmarkTimer{" +
                "meanRuntime=%s ms" +
                ", stdError=%s ms" +
                ", stdRuntime=%s ms" +
                ", totalRuntime=%s ms" +
                '}', (float) meanRuntime / 1000000, (float) stdError / 1000000, (float) stdRuntime / 1000000, (float) totalRuntime / 1000000);
    }
}
