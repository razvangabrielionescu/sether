"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var router_1 = require('@angular/router');
var shared_1 = require('../../shared');
var _1 = require('./');
var ENTITY_STATES = _1.projectRoute.concat(_1.projectPopupRoute);
var SpongeProjectModule = (function () {
    function SpongeProjectModule() {
    }
    SpongeProjectModule = __decorate([
        core_1.NgModule({
            imports: [
                shared_1.SpongeSharedModule,
                router_1.RouterModule.forRoot(ENTITY_STATES, { useHash: true })
            ],
            declarations: [
                _1.ProjectComponent,
                _1.ProjectDetailComponent,
                _1.ProjectDialogComponent,
                _1.ProjectDeleteDialogComponent,
                _1.ProjectPopupComponent,
                _1.ProjectDeletePopupComponent,
            ],
            entryComponents: [
                _1.ProjectComponent,
                _1.ProjectDialogComponent,
                _1.ProjectPopupComponent,
                _1.ProjectDeleteDialogComponent,
                _1.ProjectDeletePopupComponent,
            ],
            providers: [
                _1.ProjectService,
                _1.ProjectPopupService,
            ],
            schemas: [core_1.CUSTOM_ELEMENTS_SCHEMA]
        })
    ], SpongeProjectModule);
    return SpongeProjectModule;
}());
exports.SpongeProjectModule = SpongeProjectModule;
//# sourceMappingURL=project.module.js.map