<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html xmlns:form="http://www.w3.org/1999/html">
<head>
    <title>Статус платежа змінено</title>
    <jsp:include page="assets.jsp"/>

</head>
<body class="bg-light">
<div class="document">

        <c:if test="${confirmed}">
            <div class="text-nowrap" style="width: 8rem;">
                Ваш платіж був успішно підтверджений!
            </div>
        </c:if>
        <c:if test="${not confirmed}">
            <c:if test="${notes.length() > 0}">
                <div class="text-nowrap" style="width: 8rem;">
                    Ваш запит надіслано керівнику! Чекайте на відповідь!
                </div>
            </c:if>
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
  padding-top: 10px;
  padding-bottom: 10px;
  padding-left: 10px;
  padding-right: 10px;
  background-color: white;
  margin: 0 25%;
}
</style>
