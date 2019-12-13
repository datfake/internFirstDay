import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Author } from 'app/shared/model/author.model';
import { AuthorService } from './author.service';
import { AuthorComponent } from './author.component';
import { AuthorDetailComponent } from './author-detail.component';
import { AuthorUpdateComponent } from './author-update.component';
import { IAuthor } from 'app/shared/model/author.model';

@Injectable({ providedIn: 'root' })
export class AuthorResolve implements Resolve<IAuthor> {
  constructor(private service: AuthorService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAuthor> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((author: HttpResponse<Author>) => author.body));
    }
    return of(new Author());
  }
}

export const authorRoute: Routes = [
  {
    path: '',
    component: AuthorComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'loginApp.author.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AuthorDetailComponent,
    resolve: {
      author: AuthorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'loginApp.author.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AuthorUpdateComponent,
    resolve: {
      author: AuthorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'loginApp.author.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AuthorUpdateComponent,
    resolve: {
      author: AuthorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'loginApp.author.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
