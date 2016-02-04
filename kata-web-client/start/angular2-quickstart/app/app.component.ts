import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES} from 'angular2/router'
import {Injectable, Inject} from 'angular2/core';

import { HomeComponent } from './home/home';
import { Page1Component } from './page1/page1.component';
import { Page2Component } from './page2/page2.component';

@Component({
  selector: 'app',
  template: `
    <a [routerLink]="['Default']">Home</a> |
    <a [routerLink]="['Page1']">Page 1</a> |
    <a [routerLink]="['Page2']">Page 2</a>
    <hr>
    <router-outlet></router-outlet>
    `,
  directives: [ROUTER_DIRECTIVES]
})

@RouteConfig([
  { path: "/", component: HomeComponent, as: "Default" },
  { path: '/page1', as: 'Page1', component: Page1Component},
  { path: '/page2', as: 'Page2', component: Page2Component }
])

export class AppComponent {}
