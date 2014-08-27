var msgElementId = "errorMsg";
var timeout;

window.onload = function() {
	if(document.getElementById(msgElementId) != null) {
		handleLockTimeout();
	}
}

function handleLockTimeout() {
	timeout = setInterval(function() {
		var element = document.getElementById(msgElementId);

		var oldCounter = element.innerHTML.match(/\d+/);
		var newCounter = oldCounter - 1;

		if (newCounter <= 0) {
			clearInterval(timeout);
		}

		var newContent = element.innerHTML.replace(oldCounter, newCounter);

		element.innerHTML = newContent;
	}, 1000);
}