<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html xmlns:form="http://www.w3.org/1999/html">
<head>
    <title>Робота в святкові дні</title>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/css/bootstrap-select.css" />
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.1/js/bootstrap-select.min.js"></script>
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

<div class="row">

    <form action="/setHolidays" method="POST">
        <select class="mdb-select colorful-select dropdown-primary md-form"  name="dayNumber1" multiple searchable="Search here..">
            <c:forEach items="${daysArray}" var="day">
                <option value="${day}">${day}</option>
            </c:forEach>
        </select>
        <label class="mdb-main-label">Label example</label>
        <BR>
        <INPUT TYPE="SUBMIT" VALUE="Submit">
    </form>
</div>

<div class="row">
    <div class="col-md-12">

        <select class="mdb-select colorful-select dropdown-primary md-form" multiple searchable="Search here..">
            <option value="" disabled selected>Choose your country</option>
            <option value="1">USA</option>
            <option value="2">Germany</option>
            <option value="3">France</option>
            <option value="4">Poland</option>
            <option value="5">Japan</option>
        </select>
        <label class="mdb-main-label">Label example</label>
        <button class="btn-save btn btn-primary btn-sm">Save</button>

    </div>
</div>

</body>
</html>