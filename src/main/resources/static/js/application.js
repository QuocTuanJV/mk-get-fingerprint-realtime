var webSocket = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#send").prop("disabled", !connected);

    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }

    $("#responses").html("");
}

function connect() {
    webSocket = new WebSocket('ws://localhost:8082/websocket',
        'subprotocol.demo.websocket');

    webSocket.onopen = function () {
        setConnected(true);
        log('Client connection opened');

        console.log('Subprotocol: ' + webSocket.protocol);
        console.log('Extensions: ' + webSocket.extensions);
    };

    webSocket.onmessage = function (event) {

        var urlCreator = window.URL || window.webkitURL;
//               console.log( event.data);
        var datax = event.data;

        if (datax.size == 1) {
            console.log("OK OK");
            webSocket.close();
        } else {
            var imageUrl = urlCreator.createObjectURL(event.data);
            console.log(imageUrl);
            var img = document.querySelector("#imgDisplay");
            img.src = imageUrl;
        }
    };

    webSocket.onerror = function (event) {
        log('Client error: ' + event);
    };

    webSocket.onclose = function (event) {
        setConnected(false);
        log('Client connection closed: ' + event.code);
    };
}

function disconnect() {
    if (webSocket != null) {
        webSocket.close();
        webSocket = null;
    }
    setConnected(false);
}

function send() {
    const message = $("#request").val();
    log('Client sends: ' + message);
    webSocket.send(message);
}

function log(message) {
    $("#responses").append("<tr><td>" + message + "</td></tr>");
    console.log(message);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        send();
    });
});
