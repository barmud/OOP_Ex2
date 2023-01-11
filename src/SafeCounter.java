import java.util.concurrent.atomic.AtomicInteger;

public class SafeCounter {
    private final AtomicInteger counter = new AtomicInteger(0);


    /**
     * This method increments the counter value iff existingValue!=(existingValue + 1) iff only 1 Thread makes a value update
     */
    public void increment() {
        while (true) {
            int existingValue = getValue();
            int newValue = existingValue + 1;
            if (counter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }

    /**
     * Counter value getter
     * @return Value
     */
    public int getValue() {
        return counter.get();
    }

    /**
     * This method sets a new value for the counter iff existingValue!=(existingValue + valueAdd) iff only 1 Thread makes a value update
     * @param valueAdd Correct counter value
     */
    public void setValue(int valueAdd) {
        while (true) {
            int existingValue = getValue();
            int newValue = existingValue + valueAdd;
            if (counter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }
}