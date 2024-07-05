import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private api : ApiHttpService) { }

  setOnline() {
    this.api.post("/users/online");
  }

  setOffline() {
    this.api.post("/users/offline");
  }

  setAfk() {
    this.api.post("/users/afk");
  }

  setHook() {
    this.api.postHookStream("/users/hook").subscribe();
  }
}
