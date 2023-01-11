import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<V> extends FutureTask<V> implements Callable<V>, Comparable<Task<V>> {
    private TaskType priority;
    private Callable<V> callable;


    /**
     * Default constructor of Task
     * @param c the callable that will be executed in the task
     */
    private Task(Callable c) {
        super(c);
        this.callable = c;
        this.priority = TaskType.OTHER;
    }

    /**
     * Extended constructor of Task
     * @param c the callable that will be executed in the task
     * @param taskType the tasks priority
     */
    private Task(Callable<V> c , TaskType taskType) {
        super(c);
        this.callable = c;
        this.priority = taskType;
    }

    /**
     * Factory method for creating a Task from the given Callable, default priority
     * @param c the callable that will be executed in the task
     * @return new Task
     */
    public static <V> Task<V> createTask(Callable<V> c) {
        return new Task<V>(c);
    }

    /**
     * Factory method for creating a Task from the given Callable, TaskType priority
     * @param c the callable that will be executed in the task
     * @param taskType the priority of the task
     * @return new Task
     */
    public static<V> Task<V> createTask(Callable<V> c, TaskType taskType) {
        return new Task<V>(c,taskType);
    }

    /**
     * Task priority getter
     * @return the tasks priority
     */
    public TaskType getPriority() {
        return this.priority;
    }

    /**
     * Class Callable object getter
     * @return class Callable object
     */
    public Callable<V> getCallable() {
        return this.callable;
    }

    /**
     * Override Object equals() for Task objects
     * @param obj any object
     * @return true/false boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Task<?>)) return false;
        if (((Task<V>) obj).priority == this.priority && callable.equals(((Task<V>)(obj)).callable)) {return true;}
        return  false;
    }

    /**
     * Override Callable call() method
     * @return if exists a callable then callable.call(), else null
     * @throws Exception if not succeeded
     */
    @Override
    public V call() throws Exception {
        if (this.callable!=null) {
            return callable.call();
        }
        return null;
    }

    /**
     * Override Comparable compareTo() method for Task object
     * @param o the task to be compared.
     * @return comparison of the tasks priority value
     */
    @Override
    public int compareTo(Task<V> o) {
        return Integer.compare(this.priority.getPriorityValue(), o.getPriority().getPriorityValue());
    }
}
