import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class EnvService {

  constructor() {
    console.log(environment.apiUrl);
   }

  getApiUrl() : string {
    return environment.apiUrl;
  }
}
