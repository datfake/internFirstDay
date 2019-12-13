import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBook } from 'app/shared/model/book.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { BookService } from './book.service';
import { BookDeleteDialogComponent } from './book-delete-dialog.component';

@Component({
  selector: 'jhi-book',
  templateUrl: './book.component.html'
})
export class BookComponent implements OnInit, OnDestroy {
  books: IBook[];
  eventSubscriber: Subscription;
  itemsPerPage: number;
  links: any;
  page: any;
  predicate: any;
  reverse: any;
  totalItems: number;

  constructor(
    protected bookService: BookService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.books = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.reverse = true;
  }

  loadAll() {
    this.bookService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<IBook[]>) => this.paginateBooks(res.body, res.headers));
  }

  reset() {
    this.page = 0;
    this.books = [];
    this.loadAll();
  }

  loadPage(page) {
    this.page = page;
    this.loadAll();
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInBooks();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IBook) {
    return item.id;
  }

  registerChangeInBooks() {
    this.eventSubscriber = this.eventManager.subscribe('bookListModification', () => this.reset());
  }

  delete(book: IBook) {
    const modalRef = this.modalService.open(BookDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.book = book;
  }

  sort() {
    const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateBooks(data: IBook[], headers: HttpHeaders) {
    this.links = this.parseLinks.parse(headers.get('link'));
    this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
    for (let i = 0; i < data.length; i++) {
      this.books.push(data[i]);
    }
  }
}
