<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Вітаю!</title>
        <jsp:include page="assets.jsp"/>
    </head>
    <body>
    <jsp:include page="navbar.jsp"/>

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
