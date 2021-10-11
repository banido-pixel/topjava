package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MealInMemoryDaoImpl implements MealInMemoryDao {
    public final AtomicLong counter = new AtomicLong(0);
    public final int CALORIES_PER_DAY = 2000;
    private Map<Long, Meal> storage;

    public MealInMemoryDaoImpl() {
        List<Meal> meals = Arrays.asList(
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
       storage = meals.stream().collect(Collectors.toConcurrentMap(Meal::getId,meal -> meal));
    }

    public Map<Long, Meal> getAll() {
        return storage;
    }

    @Override
    public Meal getById(Long id) {
        return storage.getOrDefault(id,new Meal());
    }

    @Override
    public void add(Meal meal) {
        storage.put(meal.getId(), meal);
    }

    @Override
    public void update(Meal meal) {
        storage.put(meal.getId(), meal);
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}
