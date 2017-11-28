import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Commiter } from './commiter.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class CommiterService {

    private resourceUrl = 'sapi/commiters';

    constructor(private http: Http) { }

    create(commiter: Commiter): Observable<Commiter> {
        const copy = this.convert(commiter);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(commiter: Commiter): Observable<Commiter> {
        const copy = this.convert(commiter);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Commiter> {
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

    private convert(commiter: Commiter): Commiter {
        const copy: Commiter = Object.assign({}, commiter);
        return copy;
    }
}
