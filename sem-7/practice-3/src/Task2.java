import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class Task2 {
    public static void main(String[] args) {
        var random = new Random();

        printTask("2.1.1", () -> Observable.range(0, 1000).map(x -> random.nextInt()).map(x -> x * x));

        printTask("2.1.2", () -> Observable.range(0, 1000).map(x -> random.nextInt()).filter(x -> x > 500));

        System.out.println("2.1.3");
        System.out.println(Observable.range(0, random.nextInt(1001)).map(x -> random.nextInt()).count());

        printTask("2.2.1", () -> {
            var letters = Observable.range(0, 1000).map(x -> (char) (random.nextInt(26) + 'a'));
            var numbers = Observable.range(0, 1000).map(x -> random.nextInt(10));
            return Observable.zip(letters, numbers, (letter, number) -> letter.toString() + number);
        });

        var stream1 = Observable.range(0, 1000).map(x -> random.nextInt());
        var stream2 = Observable.range(0, 1000).map(x -> random.nextInt());

        printTask("2.2.2", () -> Observable.concat(stream1, stream2));

        printTask("2.2.3", () -> Observable.zip(stream1, stream2, Observable::just).flatMap(xs -> xs));

        printTask("2.3.1", () -> Observable.range(0, 10).map(x -> random.nextInt()).skip(3));

        printTask("2.3.2", () -> Observable.range(0, 10).map(x -> random.nextInt()).take(5));

        printTask("2.3.3", () -> Observable.range(0, random.nextInt()).map(x -> random.nextInt()).lastElement().toObservable());
    }

    private static void printTask(String taskName, Supplier<Observable<?>> observable) {
        System.out.println(taskName);
        System.out.println(String.join(", ", observable.get().map(Object::toString).collect(ArrayList<String>::new, ArrayList::add).blockingGet()));
    }
}
