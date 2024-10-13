import io.reactivex.Observable;

import java.util.Random;

public class Task2 {
    public static void main(String[] args) {
        var random = new Random();

        // 2.1.1
        Observable.range(0, 1000).map(x -> random.nextInt()).map(x -> x * x);

        // 2.1.2
        Observable.range(0, 1000).map(x -> random.nextInt()).filter(x -> x > 500);

        // 2.1.3
        Observable.range(0, random.nextInt(1001)).map(x -> random.nextInt()).count();

        // 2.2.1
        var letters = Observable.range(0, 1000).map(x -> (char)(random.nextInt(26) + 'a'));
        var numbers = Observable.range(0, 1000).map(x -> random.nextInt(10));
        Observable.zip(letters, numbers, (letter, number) -> letter.toString() + number);

        // 2.2.2
        var stream1 = Observable.range(0, 1000).map(x -> random.nextInt());
        var stream2 = Observable.range(0, 1000).map(x -> random.nextInt());
        Observable.concat(stream1, stream2);

        // 2.2.3
        Observable.zip(stream1, stream2, Observable::just).flatMap(xs -> xs);

        // 2.3.1
        Observable.range(0, 10).map(x -> random.nextInt()).skip(3);

        // 2.3.2
        Observable.range(0, 10).map(x -> random.nextInt()).take(5);

        // 2.3.3
        Observable.range(0, random.nextInt()).map(x -> random.nextInt()).lastElement().toObservable();
    }
}
