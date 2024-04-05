import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class ChannelService {

  channels : Array<any> = [
    {
      id: "a-b-c-d",
      name: "Channel 1"
    },
    {
      id: "e-f-g-h",
      name: "Channel 2"
    }
  ]

  constructor() {
    console.log(environment.apiUrl);
    console.log(environment.production);
   }

  getChannels() {
    return this.channels;
  }
}
