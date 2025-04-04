import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { EnvService } from './env.service';
import { Observable } from 'rxjs';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { KeycloakService } from 'keycloak-angular';
import { SSEKeeper } from './sse-keeper';

@Injectable({
  providedIn: 'root'
})
export class ApiHttpService {

  headers = new HttpHeaders({'Content-Type':'application/json; charset=utf-8'});

  constructor(private http: HttpClient, private env: EnvService, private ks: KeycloakService) { }

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

  public async getStream<T>(path: string, sseService: SSEKeeper) {
    const token = await this.ks.getToken();
    const url = this.env.getApiUrl() + path;
    return new Observable(
      observer => {

        var EventSource = EventSourcePolyfill; 
        let source = new EventSource(url,{
          headers: {
            'Authorization': 'Bearer ' + token ,
            'Connection': 'keep-alive',
            'Cache-Control': 'no-cache'
          },
          heartbeatTimeout: 60*60*1000,
        });

        source.onmessage = function(event: { data: unknown; }) {
          observer.next(event.data);
        }

        source.onerror = function(event) {
          observer.error(event);
          source.close()
          sseService.rebuildSSE();
        }
      }
    )
  }

  public postHookStream<T>(path: string) {
    const url = this.env.getApiUrl() + path;
    return this.http.post(url, {headers: new HttpHeaders({ "Content-Type": "text/plain" }), observe: 'events', responseType: 'text', reportProgress: true});
  }
}
