import io.reactivex.Observable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Task3 {
    private record UserFriend(int userId, int friendId) {}

    private static List<Integer> getRandomNumbers(int length, int baseBound) {
        var baseValue = ThreadLocalRandom.current().nextInt(baseBound);
        var values = IntStream.range(0, length).map(i -> baseValue + i).boxed().collect(Collectors.toList());
        Collections.shuffle(values);
        return values;
    }

    private static Observable<UserFriend> getFriends(int userId) {
        return Observable.fromIterable(getRandomNumbers(3, 9999))
                .map(friendId -> new UserFriend(userId, friendId));
    }

    public static void main(String[] args) {
        var userIds = Observable.fromIterable(getRandomNumbers(10, 9999));

        var userFriends = userIds.flatMap(Task3::getFriends);

        userFriends.subscribe(System.out::println);
    }
}
