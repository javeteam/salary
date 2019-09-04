<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html xmlns:form="http://www.w3.org/1999/html">
<head>
    <title>Підтвердіть платіж</title>
    <jsp:include page="assets.jsp"/>
    <script type="text/javascript">
        function hideIfChecked(id) {
          var x = document.getElementById(id);
          if (x.style.display === "none") {
              x.style.display = "block";
          } else {
              x.style.display = "none";
          }
      }
    </script>

</head>
<body class="bg-light">
<div class="document">

    <c:choose>
        <c:when test="${invoice.getNotes().length() > 0}">
            <c:set var="checkboxValue" value=""/>
            <c:set var="styleVar" value="display:block"/>
        </c:when>
        <c:otherwise>
            <c:set var="checkboxValue" value="checked"/>
            <c:set var="styleVar" value="display:none"/>
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
                <td><b>Бонус за управління</b></td>
                <td colspan="3">${invoice.getFormattedCurrency(invoice.getManagementBonus())}</td>
            </tr>
            <tr>
                <td><b>Зарплата на картку</b></td>
                <td colspan="3">${invoice.getFormattedCurrency(invoice.getPaymentToCard())}</td>
            </tr>
            <!--
            <tr>
                <td><b>Кількість невідгуляних днів відпустки</b></td>
                <td colspan="3">${invoice.getVacationDaysLeft()}</td>
            </tr>
            -->
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

            <c:if test="${not empty invoice.getFreelance()}">
                <tr class="table-primary">
                    <td colspan="4">Робота на фрілансі (тариф ${invoice.getFormattedCurrency(invoice.getFreelanceHourPrise())} грн/год)</td>
                </tr>
                <tr class="table-light">
                    <td></td>
                    <td><b>Період</b></td>
                    <td><b>Тривалість, днів</b></td>
                    <td><b>Вартість, грн</b></td>
                </tr>
                <c:forEach  items="${invoice.getFreelance()}" var ="freelance">
                    <tr>
                        <td></td>
                        <td>${freelance.getDatesAsString()}</td>
                        <td>${freelance.getDuration()}</td>
                        <td>${invoice.getFormattedCurrency(invoice.getAbsencePrise(freelance))}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td></td>
                    <td></td>
                    <td><b>${invoice.getAbsenceGroupDuration(invoice.getFreelance())}</b></td>
                    <td><b>${invoice.getFormattedCurrency(invoice.getAbsenceGroupPrise(invoice.getFreelance()))}</b></td>
                </tr>
            </c:if>

            <c:if test="${not empty invoice.getItems()}">
                <tr class="table-primary">
                    <td colspan="4">Додаткові нарахування та утримання</td>
                </tr>
                <tr class="table-light">
                    <td></td>
                    <td colspan="2"><b>Опис</b></td>
                    <td><b>Вартість, грн</b></td>
                </tr>
                <c:forEach var="invoiceItem" items="${invoice.getItems()}">
                    <tr>
                        <td></td>
                        <td colspan="2">${invoiceItem.getDescription()}</td>
                        <td>${invoiceItem.getPrise()}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="3"></td>
                    <td><strong>${invoice.getAdditionalItemsPrise()}</strong></td>
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
    <c:if test="${not invoice.isConfirmed()}">
    <hr>
        <form:form action="/invoiceStatusUpdate" method="POST" modelAttribute="invoice" style="margin:20px;">
            <form:hidden path="uuid" />
            <div class="form-group">
                <form:checkbox path="confirmed" onchange="hideIfChecked('comment')" value="Платіж підтверджений" checked="${checkboxValue}"/><strong> Я підтверджую, що всі наведені дані вірні</strong>
            </div>
            <div class="form-group" id="comment" style="${styleVar}">
                <form:textarea class="form-control" path="notes" rows="3" placeholder="Перелічіть всі виявлені неточності"/>
            </div>
            <button class="btn btn-primary custom-centered" style="display:table;" type="submit">Зберегти зміни</button>
        </form:form>
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
</style>
