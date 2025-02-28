import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';
import { Listener } from '../models/listener';
import { HttpDownloadProgressEvent, HttpEventType } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SseService {

  listeners : {[id : string] : any} = {}

  constructor(private api : ApiHttpService) {
    this.api.getStream<string>('/events').then(obs => obs.subscribe(data => {
        var key = typeof data === 'string' ? data : '';
        if (key in this.listeners) {
          this.listeners[key].notify();
        }
      })
    );
  }

  public addListener(key: string, listener : Listener) {
    this.listeners[key] = listener;
  }
}
