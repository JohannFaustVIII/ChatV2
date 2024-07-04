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
    var previous = ''; // to think: is it safe?
    this.api.getStream<Object>('/events').subscribe(data => {
        if (data.type === HttpEventType.DownloadProgress) {
          var progress = data as HttpDownloadProgressEvent;
          var toAnalyze = progress.partialText?.substring(previous.length);
          previous = typeof progress.partialText === 'string' ? progress.partialText : '';
          console.log(toAnalyze);
          var key = typeof data === 'string' ? data : '';
          if (key in this.listeners) {
            this.listeners[key].notify();
          }
        }
    });
  }

  public addListener(key: string, listener : Listener) {
    this.listeners[key] = listener;
  }
}
