import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private api : ApiHttpService) { }

  getMessages(channelName : string) {
    return this.api.get<Array<Message>>('/chat/' + channelName);
  }

  addMessage(channelName : string, message : any) {
    this.api.post('/chat/' + channelName, message);
  }
}
