"use strict";
var ResponseWrapper = (function () {
    function ResponseWrapper(headers, json, status) {
        this.headers = headers;
        this.json = json;
        this.status = status;
    }
    return ResponseWrapper;
}());
exports.ResponseWrapper = ResponseWrapper;
//# sourceMappingURL=response-wrapper.model.js.map