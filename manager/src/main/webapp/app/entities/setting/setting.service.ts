import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Setting } from './setting.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class SettingService {

    private resourceUrl = 'sapi/settings';

    constructor(private http: Http) { }

    create(setting: Setting): Observable<Setting> {
        const copy = this.convert(setting);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(setting: Setting): Observable<Setting> {
        const copy = this.convert(setting);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Setting> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convert(setting: Setting): Setting {
        const copy: Setting = Object.assign({}, setting);
        return copy;
    }
}
