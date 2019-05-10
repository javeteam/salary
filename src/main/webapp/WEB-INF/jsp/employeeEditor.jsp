<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Sign in</title>
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
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/welcome">Aspect Translation Company</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Платежі
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <c:if test="${not paymentExist}">
                        <a class="dropdown-item" href="/createPayment">Новий платіж</a>
                    </c:if>
                    <a class="dropdown-item" href="/paymentList">Платежі</a>
                </div>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Працівники
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <a class="dropdown-item" href="/createEmployee">Додати працівника</a>
                    <a class="dropdown-item" href="/employeeList">Керування працівниками</a>
                </div>
            </li>

            <li class="nav-item">
                <a class="nav-link" href="/logout">Змінити пароль</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/logout">Вийти</a>
            </li>
        </ul>
    </div>
</nav>
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
        <form action="/saveEmployee" method="POST" style="width:50%; margin:inherit;">
            <div class="form-group" style="display:none;">
                <input type="text" class="form-control" id="employeeId" placeholder=${employee.getId()}>
            </div>
            <div class="form-group">
                <label for="name"><strong>Ім'я</strong></label>
                <input type="text" class="form-control" id="name" placeholder=${employee.getName()}>
            </div>
            <div class="form-group">
                <label for="surname"><strong>Прізвище</strong></label>
                <input type="text" class="form-control" id="surname" placeholder=${employee.getSurname()}>
            </div>
            <div class="form-group">
                <label for="xtrfName"><strong>Ім'я користувача в XTRF (без Freelance/Overtime)</strong></label>
                <input type="text" class="form-control" id="xtrfName" placeholder=${employee.getXtrfName()}>
            </div>
            <div class="form-group">
                <label for="email"><strong>Поштова скринька</strong></label>
                <input type="text" class="form-control" id="email" placeholder=${employee.getEmail()}>
            </div>
            <div class="form-group">
                <label for="bitrixId"><strong>Ідентификітор корустувача в Бітрікс</strong></label>
                <input type="text" class="form-control" id="bitrixId" placeholder=${employee.getBitrixUserId()}>
            </div>
            <div class="form-group">
                <label for="position"><strong>Посада</strong></label>
                <select class="form-control" id="position">
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
                </select>
            </div>
            <div class="form-group">
                <label for="salary"><strong>Зарплатня</strong></label>
                <input type="text" class="form-control" id="salary" placeholder=${Math.round(employee.getSalary())}>
            </div>
            <div class="form-group">
                <label for="paymentToCard"><strong>Зарплатня на картку</strong></label>
                <input type="text" class="form-control" id="paymentToCard" placeholder=${Math.round(employee.getPaymentToCard())}>
            </div>
            <div class="form-group">
                <label for="bonus"><strong>Бонус</strong></label>
                <input type="text" class="form-control" id="bonus" placeholder=${Math.round(employee.getBonus())}>
            </div>
            <div class="form-check">
                <input class="form-check-input" type="checkbox" value="" id="userActive" ${checkboxValue}>
                <label class="form-check-label" for="userActive">
                    Працівник активний
                </label>
            </div>
        </form>
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
