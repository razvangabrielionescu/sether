import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable, Observer, Subscription } from 'rxjs/Rx';

import { WindowRef } from '../../shared/tracker/window.service';
import { CSRFService } from '../../shared/auth/csrf.service';

import * as SockJS from 'sockjs-client';
import * as Stomp from 'webstomp-client';

import { Project } from './project.model';
import { ResponseWrapper, createRequestOption } from '../../shared';
import {SystemConfiguration} from '../system-configuration/system-configuration.model';
import {TableInfo} from './sqlviewer/tableInfo.interface';
import {TableData} from './sqlviewer/tableData.interface';

@Injectable()
export class ProjectService {
    private resourceUrl = 'sapi/projects';
    private resourceFsUrl = 'sapi/projects/fs';
    private controlProjectUrl = 'sapi/projects/control';
    private projectFieldsUrl = 'sapi/project/fields';
    private ontologyFieldsUrl = 'sapi/project/ontology';
    stompClient = null;
    subscriber = null;
    connectedPromise: any;
    connection: Promise<any>;
    listener: Observable<any>;
    listenerObserver: Observer<any>;

    constructor(private http: Http,
                private $window: WindowRef,
                private csrfService: CSRFService) {
        this.connect();
        this.connection = this.createConnection();
        this.listener = this.createListener();
    }

    connect() {
        if (this.connectedPromise === null) {
            this.connection = this.createConnection();
        }

        // building absolute path so that websocket doesn't fail when deploying with a context path
        const loc = this.$window.nativeWindow.location;
        let url;
        url = '//' + loc.host + loc.pathname + 'websocket/tracker';
        const socket = new SockJS(url);
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = () => {};
        const headers = {};
        headers['X-XSRF-TOKEN'] = this.csrfService.getCSRF('XSRF-TOKEN');
        this.stompClient.connect(headers, () => {
            this.connectedPromise('success');
            this.connectedPromise = null;
            console.log('Connected to socket in projects');
        });
    }

    create(project: Project): Observable<Project> {
        const copy = this.convert(project);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    createFS(project: Project): Observable<Project> {
        const copy = this.convert(project);
        return this.http.post(this.resourceFsUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    runProject(project: Project): Observable<Project> {
        const copy = this.convert(project);
        return this.http.post(this.controlProjectUrl + '/start', copy).map((res: Response) => {
            return res.json();
        });
    }

    runProjectScheduled(project: Project, schedulePeriod: number, scheduleUnit: string): Observable<Project> {
        project.schedulePeriod = schedulePeriod;
        project.scheduleUnit = scheduleUnit;
        const copy = this.convert(project);
        return this.http.post(this.controlProjectUrl + '/start-scheduled', copy).map((res: Response) => {
            return res.json();
        });
    }

    removeProjectSchedule(project: Project): Observable<Project> {
        const copy = this.convert(project);
        return this.http.post(this.controlProjectUrl + '/remove-schedule', copy).map((res: Response) => {
            return res.json();
        });
    }

    stopProject(project: Project): Observable<Project> {
        const copy = this.convert(project);
        return this.http.post(this.controlProjectUrl + '/stop', copy).map((res: Response) => {
            return res.json();
        });
    }

    update(project: Project): Observable<Project> {
        const copy = this.convert(project);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Project> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    findProjectFields(name: string): Observable<ResponseWrapper> {
        return this.http.get(`${this.projectFieldsUrl}/${name}`)
            .map((res: Response) => this.convertResponse(res));
    }

    findOntologyFields(id: number): Observable<ResponseWrapper> {
        return this.http.get(`${this.ontologyFieldsUrl}/${id}`)
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

    receive() {
        return this.listener;
    }

    getSocialUrl(): Observable<SystemConfiguration> {
        return this.http.get('sapi/system-configurations/socialUrl').map((res: Response) => {
            return res.json();
        });
    }

    getTableInfo(project: Project): Observable<TableInfo> {
        const copy = this.convert(project);
        return this.http.post('sapi/tables', copy).map((res: Response) => {
            return res.json();
        });
    }

    getTableData(project: Project): Observable<TableData> {
        const copy = this.convert(project);
        return this.http.post('sapi/tableData', copy).map((res: Response) => {
            return res.json();
        });
    }

    runCollectorsLocally(): Observable<SystemConfiguration> {
        return this.http.get(`sapi/system-configurations/configKey/RUN_COLLECTOR_LOCAL`).map((res: Response) => {
            return res.json();
        });
    }

    subscribe() {
        this.connection.then(() => {
            this.subscriber = this.stompClient.subscribe('/topic/project', (data) => {
                if (this.listenerObserver !== undefined) {
                    this.listenerObserver.next(data);
                }
            });
        });
    }

    unsubscribe() {
        if (this.subscriber !== null) {
            this.subscriber.unsubscribe();
        }
        this.listener = this.createListener();
    }

    private createListener(): Observable<any> {
        return new Observable((observer) => {
            this.listenerObserver = observer;
        });
    }

    private createConnection(): Promise<any> {
        return new Promise((resolve, reject) => this.connectedPromise = resolve);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convert(project: Project): Project {
        const copy: Project = Object.assign({}, project);
        return copy;
    }
}
