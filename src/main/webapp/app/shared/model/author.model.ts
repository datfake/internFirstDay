import { IBook } from 'app/shared/model/book.model';

export interface IAuthor {
  id?: number;
  name?: string;
  age?: number;
  books?: IBook[];
}

export class Author implements IAuthor {
  constructor(public id?: number, public name?: string, public age?: number, public books?: IBook[]) {}
}
