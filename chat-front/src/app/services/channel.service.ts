import { Injectable } from '@angular/core';
import { Channel } from '../models/channel.model';
import { ApiHttpService } from './api-http.service';

@Injectable({
  providedIn: 'root'
})
export class ChannelService {

  channels : Array<Channel> = [
    {
      id: "a-b-c-d",
      name: "Channel 1"
    },
    {
      id: "e-f-g-h",
      name: "Channel 2"
    }
  ]

  constructor(private api: ApiHttpService) {}

  getChannels() {
    return this.api.get<Array<Channel>>('/channels');
  }
}
