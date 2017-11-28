import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Agent } from './agent.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class AgentService {

    private resourceUrl = 'sapi/agents';

    constructor(private http: Http) { }

    create(agent: Agent): Observable<Agent> {
        const copy = this.convert(agent);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(agent: Agent): Observable<Agent> {
        const copy = this.convert(agent);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Agent> {
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

    private convert(agent: Agent): Agent {
        const copy: Agent = Object.assign({}, agent);
        return copy;
    }
}
