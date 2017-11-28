import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils } from 'ng-jhipster';

import { Job } from './job.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class JobService {

    private resourceUrl = 'sapi/jobs';

    constructor(private http: Http, private dateUtils: JhiDateUtils) { }

    create(job: Job): Observable<Job> {
        const copy = this.convert(job);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    update(job: Job): Observable<Job> {
        const copy = this.convert(job);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    find(id: number): Observable<Job> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
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
        for (let i = 0; i < jsonResponse.length; i++) {
            this.convertItemFromServer(jsonResponse[i]);
        }
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convertItemFromServer(entity: any) {
        entity.startTime = this.dateUtils
            .convertLocalDateFromServer(entity.startTime);
        entity.endTime = this.dateUtils
            .convertLocalDateFromServer(entity.endTime);
    }

    private convert(job: Job): Job {
        const copy: Job = Object.assign({}, job);
        copy.startTime = this.dateUtils
            .convertLocalDateToServer(job.startTime);
        copy.endTime = this.dateUtils
            .convertLocalDateToServer(job.endTime);
        return copy;
    }
}
