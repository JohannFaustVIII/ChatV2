import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';
import { Listener } from '../models/listener';
import { HttpDownloadProgressEvent, HttpEventType } from '@angular/common/http';
import { Observable, Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SseService {

  listeners : {[id : string] : any} = {}
  sseSubscription : Subscription | null = null;

  constructor(private api : ApiHttpService) {
    this.startSSE();
  }

  public rebuildSSE() {
    if (this.sseSubscription != null) {
      this.sseSubscription.unsubscribe();
    }
    this.startSSE();
  }

  private startSSE() {
    this.api.getStream<string>('/events', this).then(obs => {
      this.sseSubscription = obs.subscribe(data => {
          var key = typeof data === 'string' ? data : '';
          if (key in this.listeners) {
            this.listeners[key].notify();
          }
        });
      }
    );
  }

  public addListener(key: string, listener : Listener) {
    this.listeners[key] = listener;
  }
}
