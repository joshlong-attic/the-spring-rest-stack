<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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
<authz:authorize ifAllGranted="ROLE_USER">

    <A href="${pageContext.request.contextPath}/signout">Sign Out</A>

</authz:authorize>

<authz:authorize ifNotGranted="ROLE_USER">

    <p>
        Please enter your username and password to log into the application.
    </p>
    <form method="post" action="${pageContext.request.contextPath}/signin">
        <DIV>
            <label style="width: 100px; display: inline-block;" class="control-label" for="username"> User Name: </label> <br/>
            <input id="username" name="username" type="text"/>

        </DIV>

        <DIV>
            <label style="width: 100px; display: inline-block" class="control-label" for="password"> Password: </label>   <br/>
            <input class="input-xlarge" id="password" name="password" type="password"/>

        </DIV>
        <input type="submit"/>

    </form>
</authz:authorize>

</body>

</html>