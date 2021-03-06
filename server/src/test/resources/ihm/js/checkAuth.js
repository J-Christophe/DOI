var path = window.location.pathname;
var page = path.split("/").pop();

if(sessionStorage.getItem("user") === null){
	window.location.href = "/ihm/authentication.html";
}
if(sessionStorage.getItem("token") === null){
	window.location.href = "/ihm/authentication.html";
}
// check if token is expired --> Request is synchronous to avoid
// loading the page before the token is checked
$.ajax({
    type: "GET",
    url: "/admin/roles/users",
    async: false,
    headers: {
        'Authorization': "Bearer " + sessionStorage.getItem("token")
    },
    crossDomain: true,
    cache: false,
    error: function (XMLHttpRequest, textStatus, errorThrown) {
    	if(XMLHttpRequest.status === 401){
    		console.log("Token expiré.");
    		sessionStorage.clear();
    		window.location.href = "/ihm/authentication.html";
    	}
    },
    success: function (token) {
        console.log("Token non expiré.");
    }
});

if(page === "administration.html" || page === "administration"){
	if(sessionStorage.getItem("admin") === "false"){
		window.location.href = "/ihm/authentication.html";
	}
} else {
	if(sessionStorage.getItem("projects") === null){
		window.location.href = "/ihm/authentication.html";
	}
}