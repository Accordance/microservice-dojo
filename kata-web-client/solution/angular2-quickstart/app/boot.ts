import {bootstrap}    from 'angular2/platform/browser'
import {provide} from 'angular2/core';
import {APP_BASE_HREF, ROUTER_PROVIDERS} from 'angular2/router';
import {HTTP_PROVIDERS} from 'angular2/http';
import { DataService} from './services/data.service';
import {AppComponent} from './app.component'

bootstrap(AppComponent, [
  ROUTER_PROVIDERS,
  HTTP_PROVIDERS,
  DataService,
  provide(APP_BASE_HREF, {useValue: '/'})
]);
