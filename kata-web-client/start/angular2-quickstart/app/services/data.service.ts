import { Injectable } from 'angular2/core';
import { Http, Response } from 'angular2/http';
import 'rxjs/add/operator/map';

@Injectable()
export class DataService {
  constructor(private _http: Http) { }

  getCharacters() {
    let observable = this._http.get('characters.json')
      .map(function(response: Response) {
        return <Character[]>response.json();
      });

    return observable;
  }

  getCharacter(id: number) {
    let observable = this._http.get('characters.json')
      .map((response: Response) => <Character[]>response.json().filter((c: Character) => c.id === id)[0]);
    return observable;
  }
}

export interface Character {
	id: number;
	name: string;
}
