import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { CommiterFieldMapping } from './commiter-field-mapping.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class CommiterFieldMappingService {

    private resourceUrl = 'sapi/commiter-field-mappings';
    private configResourceUrl = 'sapi/commiter-field-mappings/config';

    constructor(private http: Http) { }

    create(commiterFieldMapping: CommiterFieldMapping, parentId?: number): Observable<CommiterFieldMapping> {
        const copy = this.convert(commiterFieldMapping);
        copy.id = parentId;
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(commiterFieldMapping: CommiterFieldMapping): Observable<CommiterFieldMapping> {
        const copy = this.convert(commiterFieldMapping);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<CommiterFieldMapping> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    findAllForConfig(id: number): Observable<CommiterFieldMapping> {
        return this.http.get(`${this.configResourceUrl}/${id}`)
            .map((res: Response) => this.convertResponse(res));
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

    private convert(commiterFieldMapping: CommiterFieldMapping): CommiterFieldMapping {
        const copy: CommiterFieldMapping = Object.assign({}, commiterFieldMapping);
        return copy;
    }
}
