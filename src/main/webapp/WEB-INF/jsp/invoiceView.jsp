<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Sign in</title>
    <jsp:include page="assets.jsp"/>
</head>
<body class="bg-light">
<div class="document">

    <c:choose>
        <c:when test="${invoice.getAbsenceIntersection().size() == 0 }">
            <c:set var="varclass" value="thead-dark"/>
        </c:when>
        <c:otherwise>
            <c:set var="varclass" value="bg-danger"/>
        </c:otherwise>
    </c:choose>

    <table class="table table-sm" style="margin-bottom: 10px;">

        <tbody id="${loop.index}">
            <tr>
                <td><b>П.І.Б</b></td>
                <td colspan="3">${invoice.getUsername()}</td>
            </tr>
            <tr>
                <td><b>Оплатний період</b></td>
                <td colspan="3">${invoice.getPaidPeriod()}</td>
            </tr>
            <tr>
                <td><b>Дата оплати</b></td>
                <td colspan="3">${paymentDate}</td>
            </tr>
            <tr>
                <td><b>Величина заробітньої платні</b></td>
                <td colspan="3">${invoice.getFormattedCurrency(invoice.getSalary())}</td>
            </tr>
            <tr>
                <td><b>Бонус</b></td>
                <td colspan="3">${invoice.getFormattedCurrency(invoice.getBonus())}</td>
            </tr>
            <tr>
                <td><b>Зарплата на картку</b></td>
                <td colspan="3">${invoice.getFormattedCurrency(invoice.getPaymentToCard())}</td>
            </tr>

            <c:forEach  items="${invoice.getAbsenceIntersection()}" var ="intersections">
                <tr class="bg-danger">
                    <td>
                        <p><span class="fas fa-exclamation-triangle"> </span><strong> Увага, події перетинаються!</strong></p>
                    </td>
                    <td colspan="3">
                        <c:forEach  items="${intersections}" var ="intersectionItem">
                            ${invoice.getLocalizedAbsenceType(intersectionItem)} - ${intersectionItem.getDatesAsString()}
                            <br>
                        </c:forEach>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${not empty invoice.getVacation()}">
                <tr class="table-primary">
                    <td colspan="4">Відпустка (тариф ${invoice.getFormattedCurrency(invoice.getWorkingDayPrise())} грн /день)</td>
                </tr>
                <tr class="table-light">
                    <td></td>
                    <td><b>Період</b></td>
                    <td><b>Тривалість, днів</b></td>
                    <td><b>Вартість, грн</b></td>
                </tr>
                <c:forEach  items="${invoice.getVacation()}" var ="vacation">
                    <tr>
                        <td></td>
                        <td>${vacation.getDatesAsString()}</td>
                        <td>${vacation.getDuration()}</td>
                        <td>${invoice.getFormattedCurrency(invoice.getAbsencePrise(vacation))}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td></td>
                    <td></td>
                    <td><b>${invoice.getAbsenceGroupDuration(invoice.getVacation())}</b></td>
                    <td><b>${invoice.getFormattedCurrency(invoice.getAbsenceGroupPrise(invoice.getVacation()))}</b></td>
                </tr>
            </c:if>

            <c:if test="${not empty invoice.getSickLeave()}">
                <tr class="table-primary">
                    <td colspan="4">Лікарняний</td>
                </tr>
                <tr class="table-light">
                    <td></td>
                    <td><b>Період</b></td>
                    <td><b>Тривалість, днів</b></td>
                    <td><b>Вартість, грн</b></td>
                </tr>
                <c:forEach  items="${invoice.getSickLeave()}" var ="sickLeave">
                    <tr>
                        <td></td>
                        <td>${sickLeave.getDatesAsString()}</td>
                        <td>${sickLeave.getDuration()}</td>
                        <td>${invoice.getFormattedCurrency(invoice.getAbsencePrise(sickLeave))}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td></td>
                    <td></td>
                    <td><b>${invoice.getAbsenceGroupDuration(invoice.getSickLeave())}</b></td>
                    <td><b>${invoice.getFormattedCurrency(invoice.getAbsenceGroupPrise(invoice.getSickLeave()))}</b></td>
                </tr>
            </c:if>

            <c:if test="${not empty invoice.getUnpaidLeave() || not empty invoice.getOvertime()}">
                <tr class="table-primary">
                    <td colspan="4">Овертайм (тариф ${invoice.getFormattedCurrency(invoice.getOvertimeHourPrise())} грн/год) / години за свій кошт (тариф ${invoice.getFormattedCurrency(invoice.getFreelanceHourPrise())} грн/год)</td>
                </tr>
                <tr class="table-light">
                    <td><b>Тип</b></td>
                    <td><b>Період</b></td>
                    <td><b>Тривалість, годин</b></td>
                    <td><b>Вартість, грн</b></td>
                </tr>
                <c:forEach  items="${invoice.getUnpaidLeave()}" var ="unpaidLeave">
                    <tr>
                        <td>${invoice.getLocalizedAbsenceType(unpaidLeave)}</td>
                        <td>${unpaidLeave.getDatesAsString()}</td>
                        <td>${unpaidLeave.getDuration()}</td>
                        <td>${invoice.getFormattedCurrency(invoice.getAbsencePrise(unpaidLeave))}</td>
                    </tr>
                </c:forEach>
                <c:forEach  items="${invoice.getOvertime()}" var ="overtime">
                    <tr>
                        <td>${invoice.getLocalizedAbsenceType(overtime)}</td>
                        <td>${overtime.getDatesAsString()}</td>
                        <td>${overtime.getDuration()}</td>
                        <td>${invoice.getFormattedCurrency(invoice.getAbsencePrise(overtime))}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td></td>
                    <td></td>
                    <td><b>${invoice.getWeightedOvertimeAndUnpaidLeaveDuration()}</b></td>
                    <td><b>${invoice.getFormattedCurrency(invoice.getWeightedOvertimeAndUnpaidLeavePrise())}</b></td>
                </tr>
            </c:if>
            <tr class="bg-warning">
                <td></td>
                <td></td>
                <td></td>
                <td ><b>${invoice.getFormattedCurrency(invoice.getTotalAmount())}</b></td>
            </tr>
        </tbody>
    </table>
    <hr>

    <form method="POST" action="/invoiceConfirm?uuid=${invoice.getUuid()}" class="custom-centered">
        <button class="btn btn-primary" type="submit">Підтвердити</button>
    </form>
</div>


</body>
</html>

<style type="text/css">
.custom-centered{
 margin:0 auto;
 width:300px;
}

.document {
  padding-top: 10px;
  background-color: white;
  margin: 0 25%;
}
</style>
