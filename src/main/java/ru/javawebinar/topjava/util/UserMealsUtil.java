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

        List<UserMealWithExcess> mealsTo1 = filteredByCycles(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000);
        mealsTo1.forEach(System.out::println);
        System.out.println();

        List<UserMealWithExcess> mealsTo2 = filteredByStreams(meals, LocalTime.of(0, 0), LocalTime.of(23, 0), 2000);
        mealsTo2.forEach(System.out::println);
        System.out.println();

        List<UserMealWithExcess> mealsTo3 = filteredBySingleCycle(meals, LocalTime.of(0, 0), LocalTime.of(23, 1), 2000);
        mealsTo3.forEach(System.out::println);
        System.out.println();

        List<UserMealWithExcess> mealsTo4 = filteredBySingleStream(meals, LocalTime.of(0, 0), LocalTime.of(22, 0), 2000);
        mealsTo4.forEach(System.out::println);
        System.out.println();

    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesByDay.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                boolean isExcess = caloriesByDay.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExcess));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = meals.stream()
                .collect(Collectors.toMap(s -> s.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                        caloriesByDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredBySingleCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Phaser phaser = new Phaser();
        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        List<UserMealWithExcess> result = Collections.synchronizedList(new ArrayList<>());
        for (UserMeal meal : meals) {
            caloriesByDay.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);

            new Thread(() -> {
                phaser.register();
                phaser.awaitAdvance(phaser.arriveAndDeregister());
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    boolean isExcess = caloriesByDay.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                    result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExcess));
                }
            }).start();
        }
        try {
            Thread.sleep(meals.size() * 10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredBySingleStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Collector<UserMeal, HashMap<LocalDate, Map.Entry<Integer, List<UserMeal>>>, List<UserMealWithExcess>> mealsCollector = new Collector<UserMeal, HashMap<LocalDate, Map.Entry<Integer, List<UserMeal>>>, List<UserMealWithExcess>>() {
            @Override
            public Supplier<HashMap<LocalDate, Map.Entry<Integer, List<UserMeal>>>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<HashMap<LocalDate, Map.Entry<Integer, List<UserMeal>>>, UserMeal> accumulator() {
                return (map, meal) -> map.merge(meal.getDateTime().toLocalDate(), new AbstractMap.SimpleEntry<>(meal.getCalories(), new ArrayList<>()), (oldEntry, newEntry) -> {
                    oldEntry = new AbstractMap.SimpleEntry<>(oldEntry.getKey() + newEntry.getKey(), oldEntry.getValue());
                    return oldEntry;
                }).getValue().add(meal);
            }

            @Override
            public BinaryOperator<HashMap<LocalDate, Map.Entry<Integer, List<UserMeal>>>> combiner() {
                return (firstMap, secondMap) -> {
                    secondMap.forEach((localDate, mapEntry) -> firstMap.merge(localDate, mapEntry, (oldMapEntry, newMapEntry) -> {
                        oldMapEntry = new AbstractMap.SimpleEntry<>(oldMapEntry.getKey() + newMapEntry.getKey(), oldMapEntry.getValue());
                        List<UserMeal> tmpUserMealsList = new ArrayList<>();
                        tmpUserMealsList.addAll(oldMapEntry.getValue());
                        tmpUserMealsList.addAll(newMapEntry.getValue());
                        oldMapEntry = new AbstractMap.SimpleEntry<>(oldMapEntry.getKey(), tmpUserMealsList);
                        return oldMapEntry;
                    }));
                    return firstMap;
                };
            }

            @Override
            public Function<HashMap<LocalDate, Map.Entry<Integer, List<UserMeal>>>, List<UserMealWithExcess>> finisher() {
                return map -> map.values().stream()
                        .flatMap(l -> l.getValue().stream()
                                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                                .map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                                        l.getKey() > caloriesPerDay)))
                        .collect(Collectors.toList());
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }

        };

        return meals.parallelStream().collect(mealsCollector);
    }
}
