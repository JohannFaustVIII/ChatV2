import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';
import { UserDetails } from '../models/userDetails.model';
import { SSEKeeper } from './sse-keeper';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService implements SSEKeeper {

  sseSubscription : Subscription | null = null;
  
  constructor(private api : ApiHttpService) { }
  
  rebuildSSE(): void {
    if (this.sseSubscription != null) {
      this.sseSubscription.unsubscribe();
    }
    this.setHook();
  }

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
    this.api.getStream<string>("/users/hook", this).then(obs => {
      this.sseSubscription = obs.subscribe(data => {});
      }
    );
  }

  getUsers() {
    return this.api.get<{[key : string] : UserDetails[]}>('/users');
  }
}
