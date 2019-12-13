import { Component } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IBook } from 'app/shared/model/book.model';
import { BookService } from './book.service';

@Component({
  templateUrl: './book-delete-dialog.component.html'
})
export class BookDeleteDialogComponent {
  book: IBook;

  constructor(protected bookService: BookService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.bookService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'bookListModification',
        content: 'Deleted an book'
      });
      this.activeModal.dismiss(true);
    });
  }
}
