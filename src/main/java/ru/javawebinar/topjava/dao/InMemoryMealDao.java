package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMealDao implements MealDao {
    private final AtomicLong counter = new AtomicLong(0);
    private Map<Long, Meal> storage = new ConcurrentHashMap<>();

    public InMemoryMealDao() {
        List<Meal> meals = Arrays.asList(
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(counter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        meals.forEach(this::add);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Meal getById(long id) {
        return storage.getOrDefault(id, null);
    }

    @Override
    public Meal add(Meal meal) {
        if (meal.getId() == null) {
            meal.setId(counter.incrementAndGet());
            storage.put(meal.getId(), meal);
        }
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        storage.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public void delete(long id) {
        storage.remove(id);
    }
}
