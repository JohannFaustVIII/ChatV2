import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';
import { UserDetails } from '../models/userDetails.model';

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

  getUsers() {
    return this.api.get<{[key : string] : UserDetails[]}>('/users');
  }
}
