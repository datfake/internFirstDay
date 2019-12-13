import { Component } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAuthor } from 'app/shared/model/author.model';
import { AuthorService } from './author.service';

@Component({
  templateUrl: './author-delete-dialog.component.html'
})
export class AuthorDeleteDialogComponent {
  author: IAuthor;

  constructor(protected authorService: AuthorService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.authorService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'authorListModification',
        content: 'Deleted an author'
      });
      this.activeModal.dismiss(true);
    });
  }
}
