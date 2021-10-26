package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app-jdbc.xml",
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(ADMIN_MEAL2_ID, ADMIN_ID);
        assertMatch(meal, adminMeal2);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, ADMIN_ID));
    }

    @Test
    public void notFoundUserGet() {
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_MEAL2_ID, UserTestData.NOT_FOUND));
    }

    @Test
    public void otherUserGet() {
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_MEAL2_ID, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL2_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL2_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void notFoundUserDelete() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL2_ID, UserTestData.NOT_FOUND));
    }

    @Test
    public void otherUserDelete() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL2_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> all = service.getBetweenInclusive(LocalDate.of(2020, Month.OCTOBER, 30),
                LocalDate.of(2020, Month.OCTOBER, 31), USER_ID);
        assertMatch(all, userMeal2, userMeal1);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(ADMIN_ID);
        assertMatch(all, adminMeal2, adminMeal1);
    }

    @Test
    public void getAllNotFoundUser() {
        List<Meal> all = service.getAll(UserTestData.NOT_FOUND);
        assertMatch(all);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_MEAL1_ID, USER_ID), getUpdated());
    }

    @Test
    public void duplicateDateTimeUpdate() {
        Meal updated = getUpdated();
        updated.setDateTime(LocalDateTime.of(2020, Month.OCTOBER, 30, 14, 0));
        assertThrows(DataAccessException.class, () -> service.update(updated, USER_ID));
    }

    @Test
    public void otherUserUpdate() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () -> service.create(
                new Meal(LocalDateTime.of(2020, Month.OCTOBER, 30, 9, 0), "Duplicate meal",
                        500), USER_ID));
    }
}