<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Список працівників</title>
    <jsp:include page="assets.jsp"/>
    <script>
        jQuery(document).ready(function($) {
            $(".clickable-row td:not(:last-child)").click(function() {
                window.location = $(this).parent().data("href");
            });
        });

        function dismissEmployees(id){
            if (confirm("Ви впевнені, що бажаєте звільнити цього працівника?")){
                id.submit();
             } else {
                return false;
            }
        }

        function dismissEmployee(id){
            var today = new Date();
            var dd = String(today.getDate()).padStart(2, '0');
            var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
            var yyyy = today.getFullYear();
            today = dd + '.' + mm + '.' + yyyy;
            var lastDay = prompt("Вкажіть останній день роботи працівника в форматі дд.мм.рррр", today);
            if (lastDay != null && lastDay != ""){
                id.elements["dismissDate"].value = lastDay;
                id.submit();
            }
        }

        $(function() {
            $('#toggle1').change(function() {
            var url = new URL(window.location.href.toString());
            var query_string = url.search;
            var search_params = new URLSearchParams(query_string);
            search_params.set('showActiveOnly', !$('#toggle1').prop('checked'));
            url.search = search_params.toString();
            var new_url = url.toString();
            window.location.replace(new_url);
            })
        })
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
    <c:when test="${showActiveOnly}">
        <c:set var="checkboxValue" value=""/>
    </c:when>
    <c:otherwise>
        <c:set var="checkboxValue" value="checked"/>
    </c:otherwise>
</c:choose>

<div class="document">
    <div class="checkbox" style="padding-left:20px; padding-bottom:5px">
        <label>
            <input id="toggle1" type="checkbox" data-toggle="toggle" data-size="sm" ${checkboxValue}>
            Показувати неактивних працівників
        </label>
    </div>

    <c:if test="${not empty employeeList}">
        <table class="table table-sm table-striped table-dark" id="itemList">
            <thead>
            <tr>
                <th>#</th>
                <th>П.І.Б</th>
                <th>Посада</th>
                <th>Зарплатня</th>
                <th>ЗП на картку</th>
                <th>Бонус</th>
                <th>Статус</th>
                <th></th>
            </tr>
            </thead>

            <tbody>
            <c:forEach  items="${employeeList}" var ="employee" varStatus="loop">
                <form:form id="item_${loop.index}" action="/dismissEmployee" method="POST" modelAttribute="employee">
                    <input type="hidden" class="form-control-sm" name="id" value="${employee.id}" />
                    <input type="hidden" class="form-control-sm" name="dismissDate" value="${employee.dismissDate}" />
                </form:form>
                <tr class="clickable-row" data-href="/employee?id=${employee.getId()}">
                    <td>${loop.index + 1}</td>
                    <td>${employee.getSurname()} ${employee.getName()}</td>
                    <td>${employee.getPosition()}</td>
                    <td>${employee.getFormattedCurrency(employee.getSalary())}</td>
                    <td>${employee.getFormattedCurrency(employee.getPaymentToCard())}</td>
                    <td>${employee.getFormattedCurrency(employee.getBonus())}</td>
                    <td>${employee.isActive() ? "Активний" : "Не активний"}</td>
                    <td style="text-align: right; padding-right: 10px; cursor: alias;">
                        <c:choose>
                            <c:when test="${employee.isActive()}">
                                <button class="btn btn-sm" type="button" onclick="dismissEmployee(item_${loop.index})"><i class="fas fa-user-slash"></i></button>
                            </c:when>
                            <c:when test="${not empty employee.getFinalInvoiceUuid()}">
                                <button class="btn btn-sm" type="button" onclick="javascript:location.href='/invoice?uuid=${employee.getFinalInvoiceUuid()}'"><i class="far fa-calendar-alt"></i></button>
                            </c:when>
                        </c:choose>
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
