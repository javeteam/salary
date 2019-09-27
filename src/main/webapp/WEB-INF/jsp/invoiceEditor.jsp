<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html xmlns:form="http://www.w3.org/1999/html">
<head>
    <title>Коригування платежа працівника</title>
    <jsp:include page="assets.jsp"/>
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

<script type="text/javascript">
var index = "${invoice.getItems().size()}"
    $(document).ready(function(){
        $(".add-row").click(function(){
            var markup = "<tr id='item_" + index + "'>\
                        <td></td>\
                        <td colspan='2'>\
                            <input type='hidden' name='items[" + index + "].id' value='' />\
                            <input type='text' class='form-control-sm' style='width:100%' name='items[" + index + "].description' value='' />\
                        </td>\
                        <td style='white-space: nowrap;'>\
                            <input type='text' class='form-control-sm' name='items[" + index + "].prise' value='0' />\
                            <span class='fas fa-trash-alt' onclick='deleteById(item_" + index + ")' style='cursor: pointer;'></span>\
                        </td>\
                    </tr>";
            $("table tr:last").prev().prev().prev().after(markup);
            index++;
        });
    });

    function deleteById(id){
    id.remove();
    }
</script>

</head>
<body class="bg-light">
<jsp:include page="navbar.jsp"/>

<div class="document">

    <c:choose>
        <c:when test="${invoice.getAbsenceIntersection().size() == 0 }">
            <c:set var="varclass" value="thead-dark"/>
        </c:when>
        <c:otherwise>
            <c:set var="varclass" value="bg-danger"/>
        </c:otherwise>
    </c:choose>
    <c:if test="${paymentIncomplete}">
        <div style="width:100%; display: flex; justify-content:flex-end">
            <form:form action="/invoice?uuid=${invoice.getUuid()}&absencesUpdated=true" method="POST">
                <button class="btn btn-primary btn-sm" type="submit">Синхронізувати з Бітрікс</button>
            </form:form>
        </div>
    </c:if>


    <form:form id="invoiceForm" action="/invoiceUpdate" method="POST" modelAttribute="invoice" style="margin:20px;">
        <input type="hidden" class="form-control-sm" style="width:100%" name="uuid" value="${invoice.uuid}" />
        <table class="table table-sm" style="margin-bottom: 10px;">
            <tbody>
                <tr>
                    <td><b>П.І.Б</b></td>
                    <td colspan="3">
                        ${invoice.getUsername()}
                    </td>
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
                    <td colspan="3">
                        <input type="text" class="form-control-sm" style="width:100%" name="salary" value="${invoice.salary}" />
                    </td>
                </tr>
                <tr>
                    <td><b>Бонус</b></td>
                    <td colspan="3">
                        <input type="text" class="form-control-sm" style="width:100%" name="bonus" value="${invoice.bonus}" />
                    </td>
                </tr>
                <tr>
                    <td><b>Бонус за управління</b></td>
                    <td colspan="3">
                        <input type="text" class="form-control-sm" style="width:100%" name="managementBonus" value="${invoice.managementBonus}" />
                    </td>
                </tr>
                <tr>
                    <td><b>Зарплата на картку</b></td>
                    <td colspan="3">
                        <input type="text" class="form-control-sm" style="width:100%" name="paymentToCard" value="${invoice.paymentToCard}" />
                    </td>
                </tr>
                <tr>
                    <td><b>Кількість невикористаних днів відпустки</b></td>
                    <td colspan="3">
                        ${invoice.getVacationDaysLeft()}
                    </td>
                </tr>
                <c:if test="${invoice.getNotes().length() > 0}">
                    <tr>
                        <td colspan="4" class="bg-danger">
                            ${invoice.getNotes()}
                        </td>
                    </tr>
                </c:if>
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

                <tr class="table-primary">
                    <td colspan="3">Додаткові нарахування та утримання</td>
                    <td style="vertical-align:middle; text-align: right; padding-right: 10px;"><span class="fas fa-plus-circle add-row" style="cursor: pointer;"></span></td>
                </tr>
                <tr class="table-light">
                    <td></td>
                    <td colspan="2"><b>Опис</b></td>
                    <td><b>Вартість, грн</b></td>
                </tr>
                <c:forEach var="invoiceItem" items="${invoice.getItems()}" varStatus="status">
                    <tr id="item_${status.index}">
                        <td></td>
                        <td colspan="2">
                            <input type="hidden" name="items[${status.index}].id" value="${invoiceItem.id}" />
                            <input type="text" class="form-control-sm" style="width:100%" name="items[${status.index}].description" value="${invoiceItem.description}" />
                        </td>
                        <td style="white-space: nowrap;">
                            <input type="text" class="form-control-sm" name="items[${status.index}].prise" value="${invoiceItem.prise}" />
                            <span class="fas fa-trash-alt" onclick="deleteById(item_${status.index})" style="cursor: pointer;"></span>
                        </td>
                    </tr>
                </c:forEach>
                <tr class="bg-warning">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td style="max-width: 75px"><b>${invoice.getFormattedCurrency(invoice.getTotalAmount())}</b></td>
                </tr>
                <tr>
                    <td colspan="4" style="padding-top: 10px; padding-bottom: 10px;">
                        <form:checkbox path="confirmed" value="Платіж підтверджений"/><strong> Рахунок підтверждений працівником</strong>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" style="padding-top: 20px;">
                        <div style="text-align:center;">
                            <button class="btn btn-primary custom-centered" onclick="javascript:history.back()" type="button">Назад</button>
                            <c:if test="${paymentIncomplete}">
                                <button class="btn btn-primary custom-centered" type="submit">Зберегти зміни</button>
                            </c:if>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </form:form>
</div>

</body>
</html>