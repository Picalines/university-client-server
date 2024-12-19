import java.util.Scanner;
import java.util.concurrent.*;

public class Task2 {
    public static void main(String[] args) {
        System.out.println("Вводите числа, или exit для выхода");

        var scanner = new Scanner(System.in);

        while (true) {
            var userInput = scanner.nextLine();
            if (userInput.equals("exit")) {
                break;
            }

            int number;

            try {
                number = Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.err.println("error: invalid number '" + userInput + "'");
                continue;
            }

            CompletableFuture
                    .supplyAsync(() -> calculateSquare(number))
                    .thenAccept(result -> System.out.println(number + "^2 = " + result));
        }
    }

    private static int calculateSquare(int number) {
        try {
            var delayInSeconds = ThreadLocalRandom.current().nextInt(1, 6);
            Thread.sleep(delayInSeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return number * number;
    }
}
