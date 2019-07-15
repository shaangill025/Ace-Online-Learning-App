import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Tos } from 'app/shared/model/tos.model';
import { TosService } from './tos.service';
import { TosComponent } from './tos.component';
import { ITos } from 'app/shared/model/tos.model';

@Injectable({ providedIn: 'root' })
export class TosResolve implements Resolve<ITos> {
    constructor(private service: TosService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Tos> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service
                .find(id)
                .pipe(filter((response: HttpResponse<Tos>) => response.ok), map((tos: HttpResponse<Tos>) => tos.body));
        }
        return of(new Tos());
    }
}

export const tosRoute: Routes = [
    {
        path: 'tos',
        component: TosComponent,
        data: {
            authorities: [],
            pageTitle: 'smartCpdApp.tos.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
