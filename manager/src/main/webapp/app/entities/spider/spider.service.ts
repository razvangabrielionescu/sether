import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Spider } from './spider.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class SpiderService {

    private resourceUrl = 'sapi/spiders';

    constructor(private http: Http) { }

    create(spider: Spider): Observable<Spider> {
        const copy = this.convert(spider);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(spider: Spider): Observable<Spider> {
        const copy = this.convert(spider);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Spider> {
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

    private convert(spider: Spider): Spider {
        const copy: Spider = Object.assign({}, spider);
        return copy;
    }
}
