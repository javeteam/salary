<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html xmlns:form="http://www.w3.org/1999/html">
<head>
    <title>Список виплат</title>
    <jsp:include page="assets.jsp"/>
    <script>
        jQuery(document).ready(function($) {
            $(".clickable-row td:not(:last-child)").click(function() {
                window.location = $(this).parent().data("href");
            });
        });

        function deletePayment(id){
            if (confirm("Ви впевнені, що бажаєте видалити платіж?")){
                id.submit();
             } else {
                return false;
            }
        }
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

<div class="document">
    <c:if test="${not empty paymentList}">
        <table class="table-sm table-striped table-dark" style="width:100%;" id="itemList">
            <thead>
            <tr>
                <th>#</th>
                <th>Дата створення</th>
                <th>Статус</th>
                <th>Сума</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
                <c:forEach  items="${paymentList}" var ="payment" varStatus="loop">
                    <form:form id="item_${loop.index}" action="/deletePayment" method="POST" modelAttribute="payment">
                        <input type="hidden" class="form-control-sm" name="id" value="${payment.id}" />
                    </form:form>
                    <tr title="Редагувати платіж" class="clickable-row" data-href="/payment?id=${payment.getId()}">
                        <td>${loop.index + 1}</td>
                        <td>${payment.getFormattedCreationDate()}</td>
                        <td>${payment.isComplete() ? "Оплачено" : "В роботі"}</td>
                        <td>${payment.getFormattedTotalAmount()} грн</td>
                        <td style="text-align: right; padding-right: 10px;">
                            <button class="btn btn-sm" type="button" onclick="deletePayment(item_${loop.index})"><i class="fas fa-trash-alt"></i></button>
                        </td>
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
  padding-top: 40px;
  background-color: white;
  margin: 0 25%;
}

#itemList tbody tr:hover {
    cursor:pointer;
    background-color: grey;
}
</style>
