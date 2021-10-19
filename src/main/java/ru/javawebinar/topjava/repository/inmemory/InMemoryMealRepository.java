package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
        MealsUtil.mealsForSecondUser.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            return repository.merge(userId, new ConcurrentHashMap<>(), (oldMap, newMap) -> oldMap).put(meal.getId(), meal);
        }
        // handle case: update, but not present in storage
        return Optional.ofNullable(repository.get(userId)).map(map -> map.computeIfPresent(meal.getId(), (id, oldMeal) -> meal)).orElse(null);
    }

    @Override
    public boolean delete(int id, int userId) {
        return Optional.ofNullable(repository.get(userId)).map(map -> map.remove(id) != null).orElse(false);
    }

    @Override
    public Meal get(int id, int userId) {
        return Optional.ofNullable(repository.get(userId)).map(map -> map.get(id)).orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getList(userId, meal -> true);
    }

    @Override
    public List<Meal> getAllFiltered(int userId, LocalDate dateStart, LocalDate dateEnd) {
        if (dateStart == null && dateEnd == null) {
            return getAll(userId);
        }

        return getList(userId, meal -> DateTimeUtil.isBetweenClosed(meal.getDate(),
                dateStart == null ? LocalDate.MIN : dateStart,
                dateEnd == null ? LocalDate.MAX : dateEnd));
    }

    private List<Meal> getList(int userId, Predicate<Meal> filter) {
        return repository.get(userId).values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

