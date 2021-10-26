package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USERMEAL1_ID = START_SEQ + 2;
    public static final int USERMEAL2_ID = START_SEQ + 3;
    public static final int USERMEAL3_ID = START_SEQ + 4;
    public static final int USERMEAL4_ID = START_SEQ + 5;
    public static final int ADMINMEAL1_ID = START_SEQ + 6;
    public static final int ADMINMEAL2_ID = START_SEQ + 7;
    public static final int NOT_FOUND = 20;

    public static final Meal userMeal1 = new Meal(USERMEAL1_ID,
            LocalDateTime.of(2020, Month.OCTOBER, 30, 9, 0), "Завтрак", 600);
    public static final Meal userMeal2 = new Meal(USERMEAL2_ID,
            LocalDateTime.of(2020, Month.OCTOBER, 30, 14, 0), "Обед", 1200);
    public static final Meal userMeal3 = new Meal(USERMEAL3_ID,
            LocalDateTime.of(2020, Month.NOVEMBER, 30, 9, 0), "Завтрак", 500);
    public static final Meal userMeal4 = new Meal(USERMEAL4_ID,
            LocalDateTime.of(2020, Month.NOVEMBER, 30, 14, 0), "Обед", 1600);
    public static final Meal adminMeal1 = new Meal(ADMINMEAL1_ID,
            LocalDateTime.of(2020, Month.NOVEMBER, 30, 9, 0), "Завтрак Админ", 500);
    public static final Meal adminMeal2 = new Meal(ADMINMEAL2_ID,
            LocalDateTime.of(2020, Month.NOVEMBER, 30, 19, 0), "Ужин Админ", 400);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2021, Month.JUNE, 30, 14, 0), "Новая еда", 700);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal1);
        updated.setDateTime(LocalDateTime.of(2010, Month.OCTOBER, 30, 9, 0));
        updated.setDescription("Updated meal");
        updated.setCalories(222);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).isEqualTo(expected);
    }
}
