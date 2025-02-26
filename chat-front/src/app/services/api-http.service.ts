import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { EnvService } from './env.service';
import { Observable } from 'rxjs';
import 'node_modules/event-source-polyfill/src/eventsource.min.js';

declare var EventSourcePolyfill: any; // TODO: not found

@Injectable({
  providedIn: 'root'
})
export class ApiHttpService {

  headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8'});

  constructor(private http: HttpClient, private env: EnvService) { }

  public get<T>(path: string, options? : any) {
    const url = this.env.getApiUrl() + path;
    return this.http.get<T>(url, {headers: this.headers});
  }

  public post(path: string, message?: any) {
    const url = this.env.getApiUrl() + path;
    this.http.post(url, message,  {headers: this.headers})
    .subscribe({
      error: (e) => alert(e.error.message)
    });
  }

  public getStream<T>(path: string) {
    const url = this.env.getApiUrl() + path;
    return new Observable(
      observer => {

        var EventSource = EventSourcePolyfill; 
        let source = new EventSource(url,{
          // headers: {
          //   'Authorization': 'my secret jwt token'
          // }
        });
        console.log(source.withCredentials);

        source.onmessage = function(event: { data: unknown; }) {
          observer.next(event.data);
        }

        source.onerror = function(event: { data: unknown; }) {
          observer.error(event);
        }
      }
    )
  }

  public postHookStream<T>(path: string) {
    const url = this.env.getApiUrl() + path;
    return this.http.post(url, {headers: new HttpHeaders({ "Content-Type": "text/plain" }), observe: 'events', responseType: 'text', reportProgress: true});
  }
}
