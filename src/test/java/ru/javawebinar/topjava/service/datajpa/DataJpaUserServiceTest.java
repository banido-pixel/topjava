package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.NOT_FOUND;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends UserServiceTest {

    @Autowired
    protected MealService mealService;

    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
        MEAL_MATCHER.assertMatch(user.getMeals(), Arrays.asList(adminMeal2, adminMeal1));
    }

    @Test
    public void getWithMealsNotFound() {
        assertThrows(NotFoundException.class, () -> service.getWithMeals(NOT_FOUND));
    }

    @Test
    public void getWithEmptyMeals() {
        mealService.delete(ADMIN_MEAL_ID, ADMIN_ID);
        mealService.delete(ADMIN_MEAL_ID + 1, ADMIN_ID);
        User user = service.getWithMeals(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
        MEAL_MATCHER.assertMatch(user.getMeals(), Collections.emptyList());
    }
}
