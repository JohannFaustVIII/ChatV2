import { Injectable } from '@angular/core';

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

  constructor() {}

  getChannels() {
    return this.channels;
  }
}
