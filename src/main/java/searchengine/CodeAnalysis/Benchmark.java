package searchengine.CodeAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * <pre>
 * This class gives a benchmark with average runtime, total run time and 95% confidence interval of the average
 * of all run times
 * </pre>
 */
class Benchmark {
    private final Callable callable;
    private final int warmUpIterations;
    private final int iterations;
    private final List<Long> runTimes = new ArrayList<>();
    private long meanRuntime;
    private long totalRuntime;
    private long stdRuntime;
    private long confInterval;

    /**
     * <pre>
     * Create a benchmark of a method.
     *
     * @param callable         An implementation of the Callable interface. The Call() method is the
     *                         function called when benchmarking.
     * @param iterations       The number of iterations the benchmark is based on.
     * @param warmUpIterations The number of warm up iterations used for the benchmark.
     * @throws Exception If the Callable method throws an exception it needs to be caught.
     * </pre>
     */
    public Benchmark(Callable callable, int iterations, int warmUpIterations) throws Exception {
        this.callable = callable;
        this.warmUpIterations = warmUpIterations;
        this.iterations = iterations;
        if (iterations <= 1) {
            System.out.println("Error in Benchmark: iterations must be 2 or larger");
            return;
        }
        run();
    }

    /**
     * <pre>
     * Generates a list of queries, each containing a specified number of words. Words are selected using seed(0) so
     * this method will generate the same list each time as long as the input arguments are the same.
     *
     * @param wordList        The list of words to build the random queries from. (words are selected random)
     * @param wordsPerQuery   How many words should each query contain
     * @param numberOfQueries How many random queries do you want to generate
     * @return A list of string in the format af multi-word queries (without any OR statements)
     * </pre>
     */
    public static List<String> generateQueryList(ArrayList<String> wordList, int wordsPerQuery, int numberOfQueries) {
        Random rnd = new Random();
        rnd.setSeed(0);
        List<String> queryList = new ArrayList<>();
        for (int i = 0; i < numberOfQueries; i++) {
            List<String> queryWords = new ArrayList<>();

            for (int j = 0; j < wordsPerQuery; j++) {
                queryWords.add(wordList.get(rnd.nextInt(wordList.size())));
            }
            queryList.add(String.join(" ", queryWords));
        }
        return queryList;
    }

    /**
     * <pre>
     * @return The average run time when Callable is called.
     * </pre>
     */
    public long getMeanRuntime() {
        return meanRuntime;
    }

    /**
     * <pre>
     * @return The total runtime of all iterations
     * </pre>
     */
    public long getTotalRuntime() {
        return totalRuntime;
    }

    /**
     * <pre>
     * @return The standard deviation of all calls
     * </pre>
     */
    public long getStdRuntime() {
        return stdRuntime;
    }

    /**
     * <pre>
     * The standard deviation of the average runtime (standard deviation divided by sqrt(iterations)
     *
     * @return standard error on the mean.
     * </pre>
     */
    public long getConfInterval() {
        return confInterval;
    }

    /**
     * <pre>
     * This method runs the actual benchmark.
     * For the calculation of the 95% confidence interval see https://en.wikipedia.org/wiki/1.96
     *
     * @throws Exception If the Callable method throws an exception it needs to be caught.
     * </pre>
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

        confInterval = (long) (stdRuntime / Math.sqrt(runTimes.size() - 1) * 1.96);
    }

    @Override
    public String toString() {
        return String.format("Benchmark{" +
                "meanRuntime=%s ms" +
                ", 95%%confInterval=+/-%s ms" +
                ", std=%s ms" +
                ", totalRuntime=%s ms" +
                '}', (float) meanRuntime / 1000000, (float) confInterval / 1000000, (float) stdRuntime / 1000000, (float) totalRuntime / 1000000);
    }
}
