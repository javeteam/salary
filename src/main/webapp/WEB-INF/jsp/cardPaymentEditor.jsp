<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Інформація про нарахування на картку</title>
    <jsp:include page="assets.jsp"/>
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
        <h3 style="text-align: center;">Редагування інформації про нарахування на картку</h3>
        <hr>
        <form:form method="post" action="/updateCardPayment" modelAttribute="session">
            <table class="table table-sm table-striped">
                <tbody>
                <c:forEach var="employee" items="${session.getEmployeeCardPayments()}" varStatus="status">
                    <tr>
                        <th>${status.index + 1}</th>
                        <td>${employee.getSurname()} ${employee.getName()}</td>
                        <td>
                            <input type="hidden" class="form-control-sm" style="width:100%" name="employeeCardPayments[${status.index}].id" value="${employee.id}" />
                            <input type="text" class="form-control-sm" style="width:100%" name="employeeCardPayments[${status.index}].paymentToCard" value="${employee.paymentToCard}" />
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <button class="btn btn-primary custom-centered" style="display:table;" type="submit">Зберегти зміни</button>
        </form:form>
    </div>
</div>
</body>
</html>


