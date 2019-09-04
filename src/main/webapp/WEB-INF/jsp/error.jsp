<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html xmlns:form="http://www.w3.org/1999/html">
<head>
    <title>Помилка</title>
    <jsp:include page="assets.jsp"/>

    <style type="text/css">
        .document {
        padding-top: 10px;
        padding-bottom: 10px;
        padding-left: 10px;
        padding-right: 10px;
        background-color: white;
        margin: 0 25%;
    }
    </style>

</head>
<body class="bg-light">
<div class="document">
    <div class="page-wrap d-flex flex-row align-items-center">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-12 text-center">
                    <span class="display-1 d-block">404</span>
                    <div class="mb-4 lead">Дуже шкода, але сторінка, яку ви шукаєте була переміщена, чи ніколи не існувала.</div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>


