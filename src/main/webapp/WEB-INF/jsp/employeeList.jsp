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

<div class="document">
    <c:if test="${not empty employeeList}">
        <table class="table-sm table-striped table-dark" style="width:100%;" id="itemList">
            <thead>
            <tr>
                <th>#</th>
                <th>П.І.Б</th>
                <th>Посада</th>
                <th>Зарплатня</th>
                <th>Зарплатня на картку</th>
                <th>Бонус</th>
                <th>Статус</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach  items="${employeeList}" var ="employee" varStatus="loop">
                <tr class="clickable-row" data-href="/employee?id=${employee.getId()}">
                    <td>${loop.index + 1}</td>
                    <td>${employee.getSurname()} ${employee.getName()}</td>
                    <td>${employee.getPosition()}</td>
                    <td>${employee.getFormattedCurrency(employee.getSalary())}</td>
                    <td>${employee.getFormattedCurrency(employee.getPaymentToCard())}</td>
                    <td>${employee.getFormattedCurrency(employee.getBonus())}</td>
                    <td>${employee.isActive() ? "Активний" : "Не активний"}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
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
