var ws = new WebSocket('ws://' + location.hostname + ':' + location.port + '/socket');

setInterval(function() {
    ws.send("Heartbeat");
}, 5000);

ws.onmessage = function (event) {
    var update = JSON.parse(event.data);
    console.log(update);
    switch (update.type) {
        case "INITIAL-STATE":
            processInitialState(update.state);
            drawUI();
            break;
        case "STATE-UPDATE":
            processUpdate(update.update);
            updateUI();
            break;
        case "CURRENT-STATE":
            if (processState(update.state)) {
                drawUI();
            }
            break;
        case "ERROR-MESSAGE":
            console.log(update.message);
            processError(update.message);
            break;
    }
};

function fetchCurrentState() {
    fetch(location.protocol + '//' + location.hostname + ':' + location.port + '/get-current-state')
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error();
            }
            return response.json();
        })
        .then(data => {
            if (data == "") {
                return;
            }
            state = data;
            computeState();
            drawUI();  
        })
        .catch(error => {
            console.log(error)
            state = null;
            drawUI();  
        });  
}