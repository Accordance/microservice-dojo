import { Component, OnInit } from 'angular2/core';
import { Injectable, Inject } from 'angular2/core';
import { Router } from 'angular2/router';

import { DataService, Character } from '../services/data.service';

@Component({
  selector: 'page2',
  templateUrl: 'app/page2/page2.component.html',
  styleUrls: ['app/page2/page2.component.css']
})
export class Page2Component implements OnInit {
  characters: Character[];
  currentCharacter: Character;

  constructor(private _dataService: DataService) { }

  ngOnInit() {
    this.characters = this.getCharacters();
  }

  onSelect(character: Character) {
    this.currentCharacter = character;
    this._dataService.getCharacter(character.id)
      .subscribe((character: Character) => {
        this.currentCharacter = character;
      });
  }

  /////////////////

  getCharacters() {
    this.currentCharacter = undefined;
    this.characters = [];

    this._dataService.getCharacters()
      .subscribe((characters: Character[]) => {
        this.characters = characters;
      });

    return this.characters;
  }
}
