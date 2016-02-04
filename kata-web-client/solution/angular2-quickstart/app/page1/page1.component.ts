import {Component} from 'angular2/core';

@Component({
  selector: 'my-dashboard',
	template: `
		<h2>Page 1</h2>
		<div>Hello, {{name}}!</div>
		<input [(ngModel)]="name" />
		<button (click)="sayHello()">Say Hello</button>
		<p>{{message}}</p>
	`
})
export class Page1Component {
	name = 'world';
	message = '';

	sayHello() {
		this.message = 'Hello, ' + this.name + '! For real this time.';
	}
}
