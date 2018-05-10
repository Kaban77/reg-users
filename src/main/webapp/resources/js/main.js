function sendRequest(method, controller, data, callback) {
    const url = `./${controller}`;
    const request = new XMLHttpRequest();

    if (method === "GET") {
        request.open(method, url + data, true);
    }
    else {
        request.open(method, url, true);
    }
    request.setRequestHeader("content-type", "application/json");
    request.contentType = "application/json";
    request.setRequestHeader("cache-control", "no-cache");

    if (data !== null && method !== "GET") {
        request.send(JSON.stringify(data));
    }
    else
        request.send();

    request.onreadystatechange = () => {
        if (request.readyState !== 4)
            return;

        if (request.status >= 200 && request.status < 300) {
            if (callback !== undefined && callback !== null) {
                try {
                    const result = JSON.parse(request.responseText);
                    callback(result);
                }
                catch(e) {
                    callback();
                }
            }
        } else {
            console.log(`${request.status}: ${request.statusText}`);
        }
    };
}