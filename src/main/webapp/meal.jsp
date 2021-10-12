<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>MealForm</title>
</head>
<body>

<jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
<h2>${(meal.id != null) ? 'Update meal' : 'Add new meal'} </h2>
<form method="post" action="meals">
    <input type="hidden" name="mealId" value=${meal.id}>
    <p>DateTime : <input type="datetime-local" name="dateTime" value=${meal.dateTime}></p>
    <p>Description : <input type="text" name="description" value=${meal.description}></p>
    <p>Calories : <input type="number" name="calories" value=${meal.calories}></p>
    <p>
        <button type="submit">Confirm</button>
        <button onclick="location.href='meals'" type="button">Cancel</button>
    </p>
</form>

</body>
</html>
