/*global cordova, module*/

function InAppBrowserXwalk() {

}

var callbacks = new Array ();

InAppBrowserXwalk.prototype = {
    close: function () {
        cordova.exec(null, null, "InAppBrowserXwalk", "close", []);
    },
    addEventListener: function (eventname, func) {
        callbacks[eventname] = func;
    },
    removeEventListener: function (eventname) {
        callbacks[eventname] = undefined;
    },
    show: function () {
        cordova.exec(null, null, "InAppBrowserXwalk", "show", []);
    },
    hide: function () {
        cordova.exec(null, null, "InAppBrowserXwalk", "hide", []);
    },
    executeScript: function (javascript, options) {
        options = (options === undefined) ? "{}" : JSON.stringify(options);
        cordova.exec(null, null, "InAppBrowserXwalk", "executeScript", [javascript, options]);
    },
    injectStyleCode: function (style, options) {
        options = (options === undefined) ? "{}" : JSON.stringify(options);
        cordova.exec(null, null, "InAppBrowserXwalk", "injectStyleCode", [style, options]);
    }
}

var callback = function(event) {

    if (event.type === "loadstart" && callbacks['loadstart'] !== undefined) {
        callbacks['loadstart'](event.url);
    }
    if (event.type === "loadstop" && callbacks['loadstop'] !== undefined) {
        callbacks['loadstop'](event.url);
    }
    if (event.type === "shouldInterceptLoadRequest" && callbacks['shouldInterceptLoadRequest'] !== undefined) {
        callbacks['shouldInterceptLoadRequest'](event.url);
    }
    if (event.type === "exit" && callbacks['exit'] !== undefined) {
        callbacks['exit']();
    }
    if (event.type === "executeScript" && callbacks['executeScript'] !== undefined) {
        callbacks['executeScript'](event.javascript);
    }
    if (event.type === "injectStyleCode" && callbacks['injectStyleCode'] !== undefined) {
        callbacks['injectStyleCode'](event.style);
    }
}

module.exports = {
    open: function (url, options) {
        options = (options === undefined) ? "{}" : JSON.stringify(options);
        cordova.exec(callback, null, "InAppBrowserXwalk", "open", [url, options]);
        return new InAppBrowserXwalk();
    },
    loadFromManifest: function (url, options) {
        options = (options === undefined) ? "{}" : JSON.stringify(options);
        cordova.exec(callback, null, "InAppBrowserXwalk", "loadFromManifest", [url, options]);
        return new InAppBrowserXwalk();
    }
};
