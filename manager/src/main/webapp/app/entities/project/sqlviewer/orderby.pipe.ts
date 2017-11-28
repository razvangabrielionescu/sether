import { Pipe, PipeTransform } from '@angular/core';
import {TableCell} from './tableCell.interface';
import {TableRow} from './tableRow.interface';

@Pipe({  name: 'orderBy' })
export class OrderByPipe implements PipeTransform {

    transform(records: Array<any>, args?: any): any {
        return records.sort(function(a, b){
            const cella: TableCell = this.getCell(a, args.property);
            const cellb: TableCell = this.getCell(b, args.property);
            if (!cella || !cellb) {
                return 0;
            }
            if (cella.data < cellb.data) {
                return -1 * args.direction;
            } else if (cella.data > cellb.data) {
                return 1 * args.direction;
            } else {
                return 0;
            }
        });
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
}
