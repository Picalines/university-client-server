import io.reactivex.Completable;
import io.reactivex.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

enum AlarmLevel {
    Fine,
    TemperatureWarning,
    SmokeWarning,
    Emergency,
}

public class Task1 {
    private static final int MAX_TEMPERATURE = 25;

    private static final int MAX_CO2 = 70;

    public static void main(String[] args) {
        var random = new Random();

        var temperature = Observable.interval(1, TimeUnit.SECONDS)
                .map(t -> random.nextInt(15, 31));

        var co2 = Observable.interval(1, TimeUnit.SECONDS)
                .map(t -> random.nextInt(30, 101));

        temperature.subscribe(t -> System.out.println("temperature: " + t));
        co2.subscribe(smoke -> System.out.println("co2: " + smoke));

        var alarmLevel = Observable
                .combineLatest(temperature, co2, (temp, smoke) -> {
                    var temperatureAtRisk = temp > MAX_TEMPERATURE;
                    var smokeAtRisk = smoke > MAX_CO2;

                    if (temperatureAtRisk && smokeAtRisk) {
                        return AlarmLevel.Emergency;
                    }

                    if (temperatureAtRisk) {
                        return AlarmLevel.TemperatureWarning;
                    }

                    if (smokeAtRisk) {
                        return AlarmLevel.SmokeWarning;
                    }

                    return AlarmLevel.Fine;
                });

        alarmLevel.subscribe(level -> {
            switch (level) {
                case TemperatureWarning -> System.out.println("WARNING: temperature level exceeded " + MAX_TEMPERATURE);
                case SmokeWarning -> System.out.println("WARNING: CO2 level exceeded " + MAX_CO2);
                case Emergency -> System.out.println("ALARM!!!");
            }
        });

        Completable.fromObservable(alarmLevel).blockingAwait();
    }
}