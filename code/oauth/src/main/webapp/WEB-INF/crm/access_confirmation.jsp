<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>
        Please authorize the client "${client.clientId}" to act on your behalf.
    </title>
</head>
<body>
<h1> Access Confirmation
</h1>


<authz:authorize ifAllGranted="ROLE_USER">


    <p>
        Do you approve <strong> ${client.clientId} </strong> with the following permissions?
    </p>

    <form action="${pageContext.request.contextPath}/oauth/authorize" method="post">

        <button type="submit" name="authorize" value="${buttonLabel}">Yes</button>

        <input name="user_oauth_approval" value="true" type="hidden"/>

        <div>
            <strong>Yes</strong>, I authorize <strong>${client.clientId}</strong> to act on my behalf. Your password will <EM>not</EM> be shared with the client.
        </div>

    </form>


    <form action="${pageContext.request.contextPath}/oauth/authorize" method="post">
        <button type="submit" name="deny" value="${buttonLabel}">
            No
        </button>
        <input name="user_oauth_approval" value="false" type="hidden"/>

        <div>
            <strong>No</strong>, I do not authorize <strong>${client.clientId}</strong> to act on my behalf.
        </div>


    </form>


</authz:authorize>
</body>
</html>



