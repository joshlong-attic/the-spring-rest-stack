<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page session="false" %>
<html>
<head>
    <title>
        Greetings!
    </title>
</head>
<body>


<sec:authorize ifAllGranted="ROLE_USER">
    <h1>Hi, <sec:authentication property="principal.username"/>!</h1>

    <form action="${pageContext.request.contextPath}/signout" method="post">
        <input type="submit" value="Sign Out"/>
        <input type="hidden" name="<c:out value="${_csrf.parameterName}"/>" value="<c:out value="${_csrf.token}"/>"/>
    </form>
</sec:authorize>

<sec:authorize ifNotGranted="ROLE_USER">
    <h1>Hello, stranger.</h1>
</sec:authorize>

</body>
</html>



