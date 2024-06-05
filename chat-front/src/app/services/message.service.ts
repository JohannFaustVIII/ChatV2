import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';
import { Message } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private api : ApiHttpService) { }

  getMessages(channelId : string) {
    return this.api.get<Array<Message>>('/chat/' + channelId + '?limit=20');
  }

  getOlderMessages(channelId: string, firstMessageId: any) {
    return this.api.get<Array<Message>>('/chat/' + channelId + '?limit=20&before=' + firstMessageId);
  }
  getMessagesAfter(channelId: string, lastMessageId: any) {
    return this.api.get<Array<Message>>('/chat/' + channelId + '?limit=20&after=' + lastMessageId);
  }

  addMessage(channelName : string, message : any) {
    this.api.post('/chat/' + channelName, message);
  }
}
