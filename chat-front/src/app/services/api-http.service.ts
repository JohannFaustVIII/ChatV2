import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { EnvService } from './env.service';

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
}
