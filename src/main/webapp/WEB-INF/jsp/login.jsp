<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Sign in</title>
        <jsp:include page="assets.jsp"/>
    </head>
    <body>

        <div class="py-5 text-center">
            <h2>Авторизуйтеся, щоб мати доступ до даних</h2>
        </div>

        <form action="/j_spring_security_check" method="POST" class="custom-centered">
            <div class="form-group" style="color:red">
            ${message}
             </div>
            <div class="form-group">
                <input type="text" name= "Login" class="form-control" id="email"
                       placeholder="Ім'я користувача">
            </div>
	        <div class="form-group">
                <input type="password" name="Password" class="form-control" id="password"
                       placeholder="Пароль">
            </div>
            <button class="btn btn-primary" type="submit">Авторизуватися</button>
        </form>


    </body>
</html>

<style type="text/css">
    .custom-centered{
        margin:0 auto;
        width:300px;
    }
</style>

