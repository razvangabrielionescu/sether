import {AfterViewInit, Component} from '@angular/core';

@Component({
    selector: 'jhi-webui-login',
    template: ``
})
export class WebuiLoginComponent implements AfterViewInit {
    constructor() { }

    ngAfterViewInit() {
        window.location.href = 'webui/index.html';
    }
}
