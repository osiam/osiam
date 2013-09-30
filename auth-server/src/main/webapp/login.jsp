<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--
  ~ Copyright (C) 2013 tarent AG
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining
  ~ a copy of this software and associated documentation files (the
  ~ "Software"), to deal in the Software without restriction, including
  ~ without limitation the rights to use, copy, modify, merge, publish,
  ~ distribute, sublicense, and/or sell copies of the Software, and to
  ~ permit persons to whom the Software is furnished to do so, subject to
  ~ the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be
  ~ included in all copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  ~ EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  ~ MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
  ~ IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
  ~ CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
  ~ TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
  ~ SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Auth-Server</title>
<link type="text/css" rel="stylesheet"
	href="<c:url value="/style.css"/>" />
</head>

<body>

	<h1>Auth-Server</h1>

	<div id="content">
		<c:if test="${not empty param.authentication_error}">
			<h1>Woops!</h1>

			<p class="error">Your login attempt was not successful.</p>
		</c:if>
		<c:if test="${not empty param.authorization_error}">
			<h1>Woops!</h1>

			<p class="error">You are not permitted to access that resource.</p>
		</c:if>

		<h2>Login</h2>

		<p>We've got 1 user: marissa. Go ahead and log in. Marissa's password is "koala".</p>
		<form id="loginForm" name="loginForm"
			action="<c:url value="/login.do"/>" method="post">
			<p>
				<label>Username: <input type='text' name='j_username' value="marissa" /></label>
			</p>
			<p>
				<label>Password: <input type='text' name='j_password' value="koala" /></label>
			</p>

			<p>
				<input name="login" value="Login" type="submit"/>
			</p>
		</form>
	</div>

</body>
</html>
