"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var project_model_1 = require('./project.model');
var ProjectPopupService = (function () {
    function ProjectPopupService(modalService, router, projectService) {
        this.modalService = modalService;
        this.router = router;
        this.projectService = projectService;
        this.isOpen = false;
    }
    ProjectPopupService.prototype.open = function (component, id) {
        var _this = this;
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;
        if (id) {
            this.projectService.find(id).subscribe(function (project) {
                _this.projectModalRef(component, project);
            });
        }
        else {
            return this.projectModalRef(component, new project_model_1.Project());
        }
    };
    ProjectPopupService.prototype.projectModalRef = function (component, project) {
        var _this = this;
        var modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static' });
        modalRef.componentInstance.project = project;
        modalRef.result.then(function (result) {
            _this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true });
            _this.isOpen = false;
        }, function (reason) {
            _this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true });
            _this.isOpen = false;
        });
        return modalRef;
    };
    ProjectPopupService = __decorate([
        core_1.Injectable()
    ], ProjectPopupService);
    return ProjectPopupService;
}());
exports.ProjectPopupService = ProjectPopupService;
//# sourceMappingURL=project-popup.service.js.map