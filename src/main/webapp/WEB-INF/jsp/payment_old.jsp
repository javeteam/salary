<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Sign in</title>
        <jsp:include page="assets.jsp"/>
    </head>
    <body class="bg-light">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <a class="navbar-brand" href="/welcome">Aspect Translation Company</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              Платежі
            </a>
            <div class="dropdown-menu" aria-labelledby="navbarDropdown">
              <c:if test="${not paymentExist}">
                <a class="dropdown-item" href="/createPayment">Новий платіж</a>
              </c:if>
              <a class="dropdown-item" href="/paymentList">Платежі</a>
            </div>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              Працівники
            </a>
            <div class="dropdown-menu" aria-labelledby="navbarDropdown">
              <a class="dropdown-item" href="/addEmployee">Додати працівника</a>
              <a class="dropdown-item" href="/employeeList">Керування працівниками</a>
            </div>
          </li>

          <li class="nav-item">
                <a class="nav-link" href="/logout">Змінити пароль</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="/logout">Вийти</a>
          </li>
        </ul>
      </div>
    </nav>
    <c:if test="${not empty missingEmployees}">
    <c:forEach  items="${missingEmployees}" var ="missingEmployee">
        <div class="alert alert-warning" role="alert">
            <strong>Увага!</strong> Користувач ${missingEmployee} відсутній чи не активний.
        </div>
    </c:forEach>
    </c:if>

    <div class="document">
    <c:if test="${not empty invoices}">
    <c:forEach  items="${invoices}" var ="invoice">
    <table class="table">
      <tbody>

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
          <td colspan="3">${invoice.getSalary()}</td>
        </tr>
        <tr>
          <td><b>Бонус</b></td>
          <td colspan="3">${invoice.getBonus()}</td>
        </tr>
        <tr>
          <td><b>Зарплата на картку</b></td>
          <td colspan="3">${invoice.getPaymentToCard()}</td>
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
          <td colspan="4">Відпустка (тариф ${invoice.getWorkingDayPrise()} грн /день)</td>
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
          <td>${invoice.getAbsencePrise(vacation)}</td>
        </tr>
        </c:forEach>
        <tr>
          <td></td>
          <td></td>
          <td><b>${invoice.getAbsenceGroupDuration(invoice.getVacation())}</b></td>
          <td><b>${invoice.getAbsenceGroupPrise(invoice.getVacation())}</b></td>
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
          <td>${invoice.getAbsencePrise(sickLeave)}</td>
        </tr>
        </c:forEach>
        <tr>
          <td></td>
          <td></td>
          <td><b>${invoice.getAbsenceGroupDuration(invoice.getSickLeave())}</b></td>
          <td><b>${invoice.getAbsenceGroupPrise(invoice.getSickLeave())}</b></td>
        </tr>
        </c:if>

        <c:if test="${not empty invoice.getUnpaidLeave() || not empty invoice.getOvertime()}">
        <tr class="table-primary">
          <td colspan="4">Овертайм (тариф ${invoice.getOvertimeHourPrise()} грн/год) / години за свій кошт (тариф ${invoice.getFreelanceHourPrise()} грн/год)</td>
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
          <td>${invoice.getAbsencePrise(unpaidLeave)}</td>
        </tr>
        </c:forEach>
        <c:forEach  items="${invoice.getOvertime()}" var ="overtime">
        <tr>
          <td>${invoice.getLocalizedAbsenceType(overtime)}</td>
          <td>${overtime.getDatesAsString()}</td>
          <td>${overtime.getDuration()}</td>
          <td>${invoice.getAbsencePrise(overtime)}</td>
        </tr>
        </c:forEach>
        <tr>
          <td></td>
          <td></td>
          <td><b>${invoice.getWeightedOvertimeAndUnpaidLeaveDuration()}</b></td>
          <td><b>${invoice.getWeightedOvertimeAndUnpaidLeavePrise()}</b></td>
        </tr>
        </c:if>
        <tr class="bg-warning">
          <td></td>
          <td></td>
          <td></td>
          <td ><b>${invoice.getTotalAmount()}</b></td>
        </tr>
      </tbody>
    </table>
    <hr/>
    </c:forEach>
    </c:if>
    <c:if test="${isDataValid}">
    <form action="new_payment/save" class="custom-centered">
         <button class="btn btn-primary" type="submit">Створити платіж</button>
    </form>
    </c:if>
    </div>

    <script type="text/javascript">
      function hideById(id) {
          var x = document.getElementById(id);
          if (x.style.display === "none") {
              x.style.display = "block";
          } else {
              x.style.display = "none";
          }
      }
    </script>
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
  margin: 0 20%;
}
</style>
