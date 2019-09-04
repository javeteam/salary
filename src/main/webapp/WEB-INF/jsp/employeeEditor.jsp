<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Коригування інформації працівника</title>
    <jsp:include page="assets.jsp"/>
    <script>
        jQuery(document).ready(function($) {
    $(".clickable-row").click(function() {
        window.location = $(this).data("href");
    });
});
    </script>
</head>
<body class="bg-light">
<jsp:include page="navbar.jsp"/>

<c:if test="${not empty missingEmployees}">
    <c:forEach  items="${missingEmployees}" var ="missingEmployee">
        <div class="alert alert-warning" role="alert">
            <strong>Увага!</strong> Користувач ${missingEmployee} відсутній чи не активний.
        </div>
    </c:forEach>
</c:if>

<c:choose>
    <c:when test="${employee.isActive()}">
        <c:set var="checkboxValue" value="checked"/>
    </c:when>
    <c:otherwise>
        <c:set var="checkboxValue" value=""/>
    </c:otherwise>
</c:choose>

<div class="document">
    <c:if test="${employee != null}">
        <form:form action="/employeeSave" method="POST" modelAttribute="employee" style="width:50%; margin:inherit;">
            <div class="form-group" style="display:none;">
                <form:input type="text" class="form-control" path="id" placeholder="${employee.getId()}"/>
            </div>
            <div class="form-group">
                <form:label path="name"><strong>Ім'я</strong></form:label>
                <form:input type="text" class="form-control" path="name" placeholder="${employee.getName()}"/>
            </div>
            <div class="form-group">
                <form:label path="surname"><strong>Прізвище</strong></form:label>
                <form:input type="text" class="form-control" path="surname" placeholder="${employee.getSurname()}"/>
            </div>
            <div class="form-group">
                <form:label path="xtrfName"><strong>Ім'я користувача в XTRF (без Freelance/Overtime)</strong></form:label>
                <form:input type="text" class="form-control" path="xtrfName" placeholder="${employee.getXtrfName()}"/>
            </div>
            <div class="form-group">
                <form:label path="email"><strong>Поштова скринька</strong></form:label>
                <form:input type="text" class="form-control" path="email" placeholder="${employee.getEmail()}"/>
            </div>
            <div class="form-group">
                <form:label path="bitrixUserId"><strong>Ідентификітор користувача в Бітрікс</strong></form:label>
                <form:input type="text" class="form-control" path="bitrixUserId" placeholder="${employee.getBitrixUserId()}"/>
            </div>
            <div class="form-group">
                <form:label path="position"><strong>Посада</strong></form:label>
                <form:select class="form-control" path="position">
                    <option>${employee.getPosition().toString()}</option>
                    <option>CEO</option>
                    <option>HR</option>
                    <option>IT</option>
                    <option>Other</option>
                    <option>PM</option>
                    <option>QA</option>
                    <option>Sales</option>
                    <option>TR</option>
                    <option>VM</option>
                </form:select>
            </div>
            <div class="form-group">
                <form:label path="salary"><strong>Зарплатня</strong></form:label>
                <form:input type="text" class="form-control" path="salary" />
            </div>
            <div class="form-group">
                <form:label path="paymentToCard"><strong>Зарплатня на картку</strong></form:label>
                <form:input type="text" class="form-control" path="paymentToCard" />
            </div>
            <div class="form-group">
                <form:label path="bonus"><strong>Бонус</strong></form:label>
                <form:input type="text" class="form-control" path="bonus" />
            </div>
            <div class="form-group">
                <form:label path="managementBonus"><strong>Бонус за управління</strong></form:label>
                <form:input type="text" class="form-control" path="managementBonus" />
            </div>
            <div class="form-group">
                <form:label path="hireDate"><strong>Перший робочий день</strong></form:label>
                <form:input class="form-control" path="hireDate" />
            </div>
            <div class="form-group">
                <form:label path="workingDayStart"><strong>Початок робочого дня</strong></form:label>
                <form:input type="time" class="form-control" path="workingDayStart" />
            </div>
            <div class="form-group">
                <form:label path="workingDayEnd"><strong>Кінець робочого дня</strong></form:label>
                <form:input type="time" class="form-control" path="workingDayEnd" />
            </div>
            <div class="form-group">
                <form:label path="lunchStart"><strong>Початок перерви</strong></form:label>
                <form:input type="time" class="form-control" path="lunchStart" />
            </div>
            <div class="form-group">
                <form:label path="lunchEnd"><strong>Кінець перерви</strong></form:label>
                <form:input type="time" class="form-control" path="lunchEnd" />
            </div>
            <div class="form-check">
                <form:checkbox path="active" value="Працівник активний"/><strong> Працівник активний</strong>
            </div>
            <hr>

            <button class="btn btn-primary custom-centered" style="display:table;" type="submit">Зберегти зміни</button>
        </form:form>
    </c:if>
</div>

</body>
</html>

<style type="text/css">
.custom-centered{
 margin:0 auto;
 width:300px;
}

.document {
  padding-top: 30px;
  padding-bottom: 30px;
  background-color: white;
  margin: 0 25%;
}

#itemList tbody tr:hover {
    cursor:pointer;
    background-color: grey;
}
</style>
