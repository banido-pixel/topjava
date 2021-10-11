package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.Map;

public interface MealInMemoryDao {
    Map<Long, Meal> getAll();

    Meal getById(Long id);

    void add(Meal meal);

    void update(Meal meal);

    void delete(Long id);
}
