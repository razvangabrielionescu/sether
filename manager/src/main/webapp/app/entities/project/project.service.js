"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var shared_1 = require('../../shared');
var ProjectService = (function () {
    function ProjectService(http) {
        this.http = http;
        this.resourceUrl = 'api/projects';
    }
    ProjectService.prototype.create = function (project) {
        var copy = this.convert(project);
        return this.http.post(this.resourceUrl, copy).map(function (res) {
            return res.json();
        });
    };
    ProjectService.prototype.update = function (project) {
        var copy = this.convert(project);
        return this.http.put(this.resourceUrl, copy).map(function (res) {
            return res.json();
        });
    };
    ProjectService.prototype.find = function (id) {
        return this.http.get(this.resourceUrl + "/" + id).map(function (res) {
            return res.json();
        });
    };
    ProjectService.prototype.query = function (req) {
        var _this = this;
        var options = shared_1.createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map(function (res) { return _this.convertResponse(res); });
    };
    ProjectService.prototype.delete = function (id) {
        return this.http.delete(this.resourceUrl + "/" + id);
    };
    ProjectService.prototype.convertResponse = function (res) {
        var jsonResponse = res.json();
        return new shared_1.ResponseWrapper(res.headers, jsonResponse, res.status);
    };
    ProjectService.prototype.convert = function (project) {
        var copy = Object.assign({}, project);
        return copy;
    };
    ProjectService = __decorate([
        core_1.Injectable()
    ], ProjectService);
    return ProjectService;
}());
exports.ProjectService = ProjectService;
//# sourceMappingURL=project.service.js.map