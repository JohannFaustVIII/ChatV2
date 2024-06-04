import { Injectable } from '@angular/core';
import { Channel } from '../models/channel.model';
import { ApiHttpService } from './api-http.service';

@Injectable({
  providedIn: 'root'
})
export class ChannelService {

  constructor(private api: ApiHttpService) {}

  getChannels() {
    return this.api.get<Array<Channel>>('/channels');
  }

  addChannel(channelName : string) {
    this.api.post('/channels', channelName);
  }
}
