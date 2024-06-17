import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private api : ApiHttpService) { }

  setOnline() {
    this.api.post("/user/active");
  }

  setOffline() {
    this.api.post("/user/offline");
  }

  setAfk() {
    this.api.post("/user/afk");
  }
}
