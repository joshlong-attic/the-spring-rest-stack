<html>
<head>
    <title>
        Sign In
    </title>
</head>
<body>
<h1> Sign In</h1>

<div>${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
</div>
<p>
    Please enter your username and password to log into the application.
</p>

<form method="post" action="${pageContext.request.contextPath}/login">
    <label class="control-label" for="j_username"> User Name: </label>
    <input id="username" name="username" type="text"/>
    <br/>

    <label class="control-label" for="password"> Password: </label>
    <input class="input-xlarge" id="password" name="password" type="password"/>
    <br/>

    <input type="submit"/>

</form>
</body>

</html>