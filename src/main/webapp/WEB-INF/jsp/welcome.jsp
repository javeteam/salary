<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Sign in</title>
        <jsp:include page="assets.jsp"/>
    </head>
    <body>
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
                <a class="dropdown-item" href="/new_payment">Новий платіж</a>
              </c:if>
              <a class="dropdown-item" href="/payments_list">Платежі</a>
            </div>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              Працівники
            </a>
            <div class="dropdown-menu" aria-labelledby="navbarDropdown">
              <a class="dropdown-item" href="#">Додати працівника</a>
              <a class="dropdown-item" href="#">Керування працівниками</a>
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
    </body>
</html>

<style type="text/css">
.custom-centered{
 margin:0 auto;
 width:300px;
}
</style>
