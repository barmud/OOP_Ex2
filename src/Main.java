import java.sql.Time;
import java.util.Timer;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        CustomExecutor customExecutor = new CustomExecutor();
        var task = Task.createTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            Thread.sleep(1000);
            return sum;
        }, TaskType.OTHER);
        var task2 = Task.createTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 5; i++) {
                sum += i;
            }
            Thread.sleep(1000);
            return sum;
        }, TaskType.COMPUTATIONAL);
        Future<Integer>[] sumTaskArray = new Future[70];
        for (int i = 0; i < 30; i++) {
            sumTaskArray[i] = customExecutor.submit(task2);
        }
        for (int i = 30; i < 70; i++) {
            sumTaskArray[i] = customExecutor.submit(task);
        }

        int[] sum = new int[70];
        try {
            int count = 0;
//            while (count !=2) {
//                if (sumTask2.isDone()) {
//                    System.out.println(sum2);
//                    count++;
//                }
//                if (sumTask.isDone()) {
//                    System.out.println(sum);
//                    count++;
//                }
            for (int i = 0; i < 70; i++) {
                sum[i] = sumTaskArray[i].get();
            }
//            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        customExecutor.gracefullyTerminate();
    }
}
