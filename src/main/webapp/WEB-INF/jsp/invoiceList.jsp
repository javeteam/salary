<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Список платехів</title>
    <jsp:include page="assets.jsp"/>
    <script>
        jQuery(document).ready(function($) {
    $(".clickable-row").click(function() {
        window.location = $(this).data("href");
    });
});
    </script>

    <style type="text/css">
        .custom-centered{
            margin:0 auto;
            width:300px;
        }

        .document {
            padding-top: 20px;
            padding-bottom: 20px;
            padding-left: 20px;
            padding-right: 20px;
            background-color: white;
            margin: 0 25%;
        }

        #itemList tbody tr:hover {
            cursor:pointer;
            background-color: grey;
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
    <c:if test="${not empty invoiceList}">
        <table class="table-sm table-striped table-dark" style="width:100%;" id="itemList">
            <thead>
            <tr>
                <th>#</th>
                <th>П.І.Б</th>
                <th>Статус</th>
                <th>Сума</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach  items="${invoiceList}" var ="invoice" varStatus="loop">
                <tr title="Редагувати платіж" class="clickable-row" data-href="/invoice?uuid=${invoice.getUuid()}">
                    <td>${loop.index + 1}</td>
                    <td>
                        <c:if test="${invoice.getAbsenceIntersection().size() > 0 }">
                            <span class="fas fa-exclamation-triangle" style="color: orange;"></span>
                        </c:if>
                        ${invoice.getUsername()}
                    </td>
                    <td>
                        <p style="margin-bottom: unset;">
                            <c:if test="${(not invoice.isConfirmed()) && (invoice.getNotes().length() > 0)}">
                                <span class="fas fa-exclamation-triangle" style="color: orange;"> </span>
                            </c:if> ${invoice.isConfirmed() ? "Підтверджено" : (invoice.getNotes().length() > 0 ? "Відхилено" : "Не підтверджено")}
                        </p>
                    </td>
                    <td>${invoice.getFormattedCurrency(invoice.getTotalAmount())} грн</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${allInvoicesConfirmed && !payment.isComplete()}">
    <hr>

    <form:form action="/paymentComplete" class="custom-centered" method="POST" modelAttribute="payment">
        <input type="hidden" name="id" value="${payment.id}" />
        <input type="hidden" name="totalAmount" value="${payment.totalAmount}" />
        <button class="btn btn-primary" style="display:table;" type="submit">Завершити платіж</button>
    </form:form>
    </c:if>
</div>

</body>
</html>

