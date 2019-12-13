import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IBook, Book } from 'app/shared/model/book.model';
import { BookService } from './book.service';

@Component({
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html'
})
export class BookUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    year: [],
    name: [null, [Validators.required]],
    author: [null, [Validators.required]]
  });

  constructor(protected bookService: BookService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ book }) => {
      this.updateForm(book);
    });
  }

  updateForm(book: IBook) {
    this.editForm.patchValue({
      id: book.id,
      year: book.year,
      name: book.name,
      author: book.author
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const book = this.createFromForm();
    if (book.id !== undefined) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  private createFromForm(): IBook {
    return {
      ...new Book(),
      id: this.editForm.get(['id']).value,
      year: this.editForm.get(['year']).value,
      name: this.editForm.get(['name']).value,
      author: this.editForm.get(['author']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
