import java.io.*;
import java.util.Random;
import java.util.concurrent.*;

public class Ex2_1 {

    /**
     * This method creates n text files, fill them with text lines using random func to determine the number of lines
     * @param n Number of files
     * @param seed Random func usage
     * @param bound Random func usage
     * @return String array of the created files names
     */
    public static String[] createTextFiles(int n, int seed, int bound) {

        String[] s = new String[n];
        Random rand = new Random(seed);
        for (int i = 1; i <= n; i++) {
            int x = rand.nextInt(bound);
            File newTextFile = new File("file_" + i + ".txt");
            FileWriter fw = null;
            try {
                fw = new FileWriter("file_" + i + ".txt");
                PrintWriter pw = new PrintWriter(fw);
                for (int j = 0; j < x; j++) {
                    pw.append("Hello World!\n");
                }
                pw.close();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            s[i - 1] = "file_" + i + ".txt";
        }
        return s;
    }

    /**
     * This method counts the number of lines in the existing text files at the given array, regular case
     * @param fileNames String array of text files names
     * @return Total number of lines
     */
    public static int getNumOfLines(String[] fileNames) {
        long startTime = System.nanoTime();
        int lines = 0;
        for (String fileName : fileNames) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                while (reader.readLine() != null) lines++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long endTime   = System.nanoTime();
        System.out.println((endTime - startTime));
        return lines;
    }


    /**
     * This method counts the number of lines in the existing text files at the given array, threads case
     * @param fileNames String array of text files names
     * @return Total number of lines
     */
    public static int getNumOfLinesThreads(String[] fileNames) {

        long startTime = System.nanoTime();
        class LinesThread extends Thread {
            public int getLines() {
                return lines;
            }

            private int lines = 0;
            private final String fileName;

            @Override
            public void run() {
                this.lines = getNumOfLines(this.fileName);
            }

            public LinesThread(String fileName) {
                this.fileName = fileName;
            }
        }
        SafeCounter linesCounter = new SafeCounter();
        LinesThread[] myThreads = new LinesThread[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            myThreads[i] = new LinesThread(fileNames[i]);
            myThreads[i].start();
        }
        for (LinesThread thread : myThreads) {
            try {
                thread.join();
                linesCounter.setValue(thread.getLines());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long endTime   = System.nanoTime();
        System.out.println((endTime - startTime));
        return linesCounter.getValue();
    }

    /**
     * This method counts the number of lines in a single text file
     * @param fileName Text file name
     * @return Number of lines in the file
     */
    public static int getNumOfLines(String fileName) {

        SafeCounter linesCounter = new SafeCounter();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.readLine() != null) linesCounter.increment();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linesCounter.getValue();
    }

    /**
     * This method counts the number of lines in a single text file, thread-pool case
     * @param fileNames String array of text files names
     * @return Total number of lines
     */
    public static int getNumOfLinesThreadPool(String[] fileNames) {

        long startTime = System.nanoTime();
        SafeCounter linesCounter = new SafeCounter();
        ExecutorService threadPool = Executors.newFixedThreadPool(fileNames.length);
        Future<Integer>[] futures = new Future[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            int finalI = i;
            Callable<Integer> getLines = ()-> getNumOfLines(fileNames[finalI]);
            futures[i] = threadPool.submit(getLines);
        }
        for (Future<Integer> future: futures) {
            try {
                linesCounter.setValue(future.get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Caught an exception: " + e.getCause());
            }
        }
        threadPool.shutdown();
        long endTime   = System.nanoTime();
        System.out.println((endTime - startTime));
        return linesCounter.getValue();
    }


    public static void main(String[] args) {
        int seed = 1000, n = 1000, bound = 10000;
        String[] st = createTextFiles(n,seed,bound);
        System.out.println("Regular:");
        System.out.println(getNumOfLines(st));
        System.out.println("Threads:");
        System.out.println(getNumOfLinesThreads(st));
        System.out.println("TheadPool:");
        System.out.println(getNumOfLinesThreadPool(st));
    }
}