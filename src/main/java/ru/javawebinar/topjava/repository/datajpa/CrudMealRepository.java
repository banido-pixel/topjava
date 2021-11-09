package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Transactional
    @Query("SELECT m FROM Meal m LEFT JOIN m.user WHERE m.id=:id AND m.user.id=:userId")
    Meal getWithUser(@Param("id") int id,@Param("userId") int userId);

    @Transactional
    Meal getByIdAndUserId(int id, int userId);

    @Transactional
    int deleteByIdAndUserId(int id, int userId);

    @Transactional
    List<Meal> getAllByUserIdOrderByDateTimeDesc(int userId);

    @Transactional
    List<Meal> getAllByDateTimeGreaterThanEqualAndDateTimeLessThanAndUserIdOrderByDateTimeDesc(
            LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);

}
