import java.util.concurrent.*;

public class CustomExecutor<V>{
    private static ThreadPoolExecutor threadpool;
    private int corePoolSize;
    private int maxPoolSize;
    private int currentMax;


    /**
     * CustomExecutor constructor
     */
    public CustomExecutor() {
        corePoolSize = Runtime.getRuntime().availableProcessors()/2;
        maxPoolSize = Runtime.getRuntime().availableProcessors()-1;
        PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>();
        threadpool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,300L, TimeUnit.MILLISECONDS,queue){

            /**
             * Override ThreadPoolExecutor beforeExecute to update currentMax value when Thread extracts a new task from the queue
             * @param t the thread that will run task {@code r}
             * @param r the task that will be executed
             */
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                Task task = (Task) queue.peek();
                if(task != null){
                    currentMax = task.getPriority().getPriorityValue();
                }
                else{
                    currentMax = 0;
                }
            }
        };

    }

    /**
     * Factory method to submit the given task to the thread-pool
     * @param task the task that will be executed
     * @return the tasks future
     */
    private <V> Future<V> submitTask(Task<V> task) {
        RunnableFuture<V> taskRunabble = task;
        threadpool.execute(taskRunabble);
        return taskRunabble;
    }

    /**
     * This method submits a new task to the thread-pool using the factory method
     * @param task the task to submit
     * @return submitTask() return future for the task
     */
    public Future<V> submit(Task task) {
            return submitTask(task);
    }

    /**
     * This method initialize a new task from the given callable&tasktype and submits it to the thread-pool using the factory method
     * @param c the callable that will be injected to the task
     * @param priority the tasktype priority that will be injected to the task
     * @return submitTask() return future for the task
     */
    public Future<V> submit(Callable c,TaskType priority) {
        Task t1 = Task.createTask(c,priority);
        return submitTask(t1);
    }


    public int getCurrenctMax() {
        return this.currentMax;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getCurrentMax() {
        return currentMax;
    }

    /**
     * This method will shut down the thread-pool and wait for all the threads to finish their work
     */
    public void gracefullyTerminate() {
        threadpool.shutdown();
        while (!threadpool.isTerminated()) {
            try {
                threadpool.awaitTermination(300, TimeUnit.MILLISECONDS); //waiting half min each time.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
