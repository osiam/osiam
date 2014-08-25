window.onload = function() {
	handleLockTimeout();
}

var timeout;

function handleLockTimeout() {
	timeout = setInterval(function() {
		var element = document.getElementById("errorMsg");

		var oldCounter = element.innerHTML.match(/\d+/);
		var newCounter = oldCounter - 1;

		if (newCounter <= 0) {
			clearInterval(timeout);
		}

		var nea = element.innerHTML.replace(oldCounter, newCounter);

		element.innerHTML = nea;
	}, 1000);
}