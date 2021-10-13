package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String ADD_OR_UPDATE = "/meal.jsp";
    private static final String MEALS_LIST = "/meals.jsp";
    private MealDao mealDao;

    @Override
    public void init() throws ServletException {
        mealDao = new InMemoryMealDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action != null) {
            switch (action.toLowerCase()) {
                case "delete":
                    log.debug("GET req : meal {} forward to delete",getId(request));
                    mealDao.delete(getId(request));
                    response.sendRedirect(request.getRequestURI());
                    return;
                case "addmeal":
                    request.setAttribute("meal", new Meal());
                    log.debug("GET req : forward to add meal form");
                    request.getRequestDispatcher(ADD_OR_UPDATE).forward(request, response);
                    return;
                case "update":
                    request.setAttribute("meal", mealDao.getById(getId(request)));
                    log.debug("GET req : meal {} forward to update meal form",getId(request));
                    request.getRequestDispatcher(ADD_OR_UPDATE).forward(request, response);
                    return;
            }
        }
        forwardToMeals(request, response);
    }

    private void forwardToMeals(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX,
                User.CALORIES_PER_DAY);
        request.setAttribute("meals", mealsTo);
        log.debug("GET req : forward to meals");
        request.getRequestDispatcher(MEALS_LIST).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String mealId = request.getParameter("mealId");

        if (mealId == null || mealId.isEmpty()) {
            log.debug("POST req : new meal forward to add");
            mealDao.add(new Meal(dateTime, description, calories));
        } else {
            log.debug("POST req : meal {} forward to update",getId(request));
            mealDao.update(new Meal(Long.parseLong(mealId), dateTime, description, calories));
        }

        response.sendRedirect(request.getRequestURI());
    }

    private long getId(HttpServletRequest request) {
        return Long.parseLong(request.getParameter("mealId"));
    }
}
