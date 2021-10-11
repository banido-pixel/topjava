package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealInMemoryDaoImpl;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String ADD_OR_UPDATE = "/meal.jsp";
    private static final String MEALS_LIST = "/meals.jsp";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final MealInMemoryDaoImpl mealDao;

    public MealServlet() {
        mealDao = new MealInMemoryDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equalsIgnoreCase(action)) {
            long mealId = Long.parseLong(request.getParameter("mealId"));
            mealDao.delete(mealId);
            response.sendRedirect(request.getRequestURI());
            return;
        } else if ("addMeal".equalsIgnoreCase(action)) {
            forwardToMeal(request, response, -1L);
        } else if ("update".equalsIgnoreCase(action)) {
            long mealId = Long.parseLong(request.getParameter("mealId"));
            forwardToMeal(request, response, mealId);
        }

        forwardToMeals(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.from(formatter.parse(request.getParameter("dateTime")));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String mealId = request.getParameter("mealId");

        if (mealId == null || mealId.isEmpty()) {
            log.debug("added meal");
            mealDao.add(new Meal(mealDao.counter.incrementAndGet(), dateTime, description, calories));
        } else {
            log.debug("updated meal");
            mealDao.update(new Meal(Long.parseLong(mealId), dateTime, description, calories));
        }

        forwardToMeals(request, response);
    }

    private void forwardToMeals(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(new ArrayList<>(mealDao.getAll().values()), LocalTime.MIN, LocalTime.MAX,
                mealDao.CALORIES_PER_DAY);
        request.setAttribute("meals", mealsTo);
        log.debug("forward to meals");
        request.getRequestDispatcher(MEALS_LIST).forward(request, response);
    }

    private void forwardToMeal(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        request.setAttribute("meal", mealDao.getById(id));
        log.debug("forward to meal");
        request.getRequestDispatcher(ADD_OR_UPDATE).forward(request, response);
    }
}
