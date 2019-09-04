<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Завантаження інформації XTRF</title>
    <jsp:include page="assets.jsp"/>
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

<div class="document">
    <div class="container">
        <h3 style="text-align: center;">Додати інформацію з XTRF</h3>
        <hr>
        <form method="POST" action="/uploadFiles" enctype="multipart/form-data">
            <div class="form-group">
                <input type="file" data-buttonText="Оберіть файл" accept=".csv" class="form-control-file" name="CSVFiles">
            </div>
            <div class="form-group">
                <input type="file" data-buttonText="Оберіть файл" accept="*.csv" class="form-control-file" name="CSVFiles">
            </div>
            <div class="form-group">
                <button type="submit" style="display: table; margin: auto;" class="btn btn-primary pull-right">Завантажити</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>

<style type="text/css">
.custom-centered{
 margin:0 auto;
 width:300px;
}

.document {
  padding-top: 20px;
  padding-bottom: 20px;
  padding-left: 10px;
  padding-right: 10px;
  background-color: white;
  margin: 0 25%;
}
</style>
