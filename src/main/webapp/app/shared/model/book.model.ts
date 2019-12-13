export interface IBook {
  id?: number;
  year?: number;
  name?: string;
  author?: string;
}

export class Book implements IBook {
  constructor(public id?: number, public year?: number, public name?: string, public author?: string) {}
}
