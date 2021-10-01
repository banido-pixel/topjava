package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

//        List<UserMealWithExcess> mealsTo1 = filteredByCycles(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000);
//        mealsTo1.forEach(System.out::println);
//        System.out.println();
//
//        List<UserMealWithExcess> mealsTo2 = filteredByStreams(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000);
//        mealsTo2.forEach(System.out::println);
//        System.out.println();

        List<UserMealWithExcess> mealsTo3 = filteredBySingleCycle(meals, LocalTime.of(0, 0), LocalTime.of(23, 1), 2000);
        mealsTo3.forEach(System.out::println);
        System.out.println();

//        List<UserMealWithExcess> mealsTo4 = filteredBySingleStream(meals, LocalTime.of(0, 0), LocalTime.of(22, 0), 2000);
//        mealsTo4.forEach(System.out::println);
//        System.out.println();

    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> checkExcessMap = new HashMap<>();
        for (UserMeal meal :
                meals) {
            checkExcessMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal :
                meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                boolean isExcess = checkExcessMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExcess));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> checkExcessMap = meals.stream()
                .collect(Collectors.toMap((s -> s.getDateTime().toLocalDate()), UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(s -> TimeUtil.isBetweenHalfOpen(s.getDateTime().toLocalTime(), startTime, endTime))
                .map(s -> new UserMealWithExcess(s.getDateTime(), s.getDescription(), s.getCalories(),
                        checkExcessMap.get(s.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredBySingleCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Phaser phaser = new Phaser();
        Map<LocalDate, Integer> checkExcessMap = new HashMap<>();
        List<UserMealWithExcess> result = Collections.synchronizedList(new ArrayList<>());
        for (UserMeal meal :
                meals) {
            checkExcessMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);

            new Thread(() -> {
                phaser.register();
                phaser.awaitAdvance(phaser.arriveAndDeregister());
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    boolean isExcess = checkExcessMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                    result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExcess));
                }
            }).start();
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredBySingleStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Collector<UserMeal, HashMap<LocalDate, List<Object>>, List<UserMealWithExcess>> mealsCollector = new Collector<UserMeal, HashMap<LocalDate, List<Object>>, List<UserMealWithExcess>>() {
            @Override
            public Supplier<HashMap<LocalDate, List<Object>>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<HashMap<LocalDate, List<Object>>, UserMeal> accumulator() {
                return (map, meal) -> {
                    map.merge(meal.getDateTime().toLocalDate(), getDefaultList(meal), (o, n) -> {
                        o.set(0, (Integer) o.get(0) + (Integer) n.get(0));
                        return o;
                    });
                    ((List<UserMeal>) map.get(meal.getDateTime().toLocalDate()).get(1)).add(meal);
                };
            }

            @Override
            public BinaryOperator<HashMap<LocalDate, List<Object>>> combiner() {
                return (l, r) -> {
                    r.forEach((k, v) -> l.merge(k, v, (o, n) -> {
                        o.set(0, (Integer) o.get(0) + (Integer) n.get(0));
                        o.set(1, ((List<UserMeal>) o.get(1)).addAll((List<UserMeal>) n.get(1)));
                        return o;
                    }));
                    return l;
                };
            }

            @Override
            public Function<HashMap<LocalDate, List<Object>>, List<UserMealWithExcess>> finisher() {
                return m -> m.values().stream()
                        .flatMap(l -> ((List<UserMeal>) l.get(1)).stream()
                                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(),
                                        ((Integer) l.get(0)) > caloriesPerDay)))
                        .collect(Collectors.toList());
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }

            private List<Object> getDefaultList(UserMeal meal){
                List<Object> result = new ArrayList<>();
                result.add(meal.getCalories());
                result.add(new ArrayList<UserMeal>());
                return result;
            }
        };

        return meals.stream().collect(mealsCollector);
    }
}
