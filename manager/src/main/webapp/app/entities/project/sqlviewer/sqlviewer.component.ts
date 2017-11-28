import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../project.service';
import { PagerService } from './pager.service';
import { Principal, ResponseWrapper } from '../../../shared';
import {TableData} from './tableData.interface';
import {TableInfo} from './tableInfo.interface';
import {JhiAlertService} from 'ng-jhipster';
import {Subscription} from 'rxjs/Subscription';
import {Project} from '../project.model';
import {TableCell} from './tableCell.interface';
import {TableRow} from './tableRow.interface';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {MoreDialogComponent} from './more-dialog.component';

@Component({
    selector: 'jhi-sqlviewer',
    templateUrl: './sqlviewer.component.html',
    styleUrls: [
        'sqlviewer.css'
    ]
})
export class SqlViewerComponent implements OnInit, OnDestroy {
    private isOpen = false;
    project: Project;
    tableInfo: TableInfo;
    tableData: TableData;
    refreshing: boolean;
    selectedCommiterConfigId: any;
    private subscription: Subscription;
    pager: any = {};
    pagedItems: any[];
    isDesc: boolean;
    scolumn: string;
    direction: number;

    constructor(
        private projectService: ProjectService,
        private alertService: JhiAlertService,
        private router: Router,
        private route: ActivatedRoute,
        private principal: Principal,
        private pagerService: PagerService,
        private modalService: NgbModal,
    ) {
    }

    ngOnInit() {
        this.projectService.subscribe();
        this.loadTable();
    }

    ngOnDestroy() {
        this.projectService.unsubscribe();
    }

    load(id) {
        this.projectService.find(id).subscribe((project) => {
            this.project = project;
            this.getTableInfo();
        });
    }

    loadTable() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
    }

    refresh() {
        this.refreshing = true;
        this.loadData();
    }

    getTableInfo() {
        this.projectService.getTableInfo(this.project).subscribe(
            (res: ResponseWrapper) => {
                this.tableInfo = res;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    tableSelected(tableName) {
        this.project.tableName = tableName;
        this.loadData();
    }

    loadData() {
        this.projectService.getTableData(this.project).subscribe(
            (res: ResponseWrapper) => {
                this.tableData = res;
                this.refreshing = false;
                this.setPage(1);
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }

    previousState() {
        window.history.back();
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
        this.refreshing = false;
    }

    setPage(page: number) {
        if (page < 1 || page > this.pager.totalPages) {
            return;
        }

        // get pager object from service
        this.pager = this.pagerService.getPager(this.tableData.rows.length, page);

        // get current page of items
        this.pagedItems = this.tableData.rows.slice(this.pager.startIndex, this.pager.endIndex + 1);
    }

    sort(property) {
        const direction = this.isDesc ? 1 : -1;
        this.isDesc = !this.isDesc;
        this.scolumn = property;

        const self = this;
        this.tableData.rows.sort(function(a, b){
            const cella: TableCell = self.getCell(a, property);
            const cellb: TableCell = self.getCell(b, property);

            if (!cella || !cellb) {
                return 0;
            }
            if (cella.data < cellb.data) {
                return -1 * direction;
            } else if (cella.data > cellb.data) {
                return 1 * direction;
            } else {
                return 0;
            }
        });

        this.setPage(this.pager.currentPage);
    };

    getCell(record: any, column: string): TableCell {
        const row: TableRow = record;
        for (const cell of row.columnData) {
            if (cell.column === column) {
                return cell;
            }
        }

        return null;
    }

    more(tableCell: TableCell) {
        this.open(MoreDialogComponent, tableCell);
    }

    open(component: Component, cell?: TableCell): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        this.moreModalRef(component, cell);
    }

    moreModalRef(component: Component, cell: TableCell): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.cell = cell;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
