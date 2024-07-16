import { Injectable } from '@angular/core';
import { ApiHttpService } from './api-http.service';
import { Message } from '../models/message.model';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private api : ApiHttpService) { }

  getMessages(channelId : string) {
    return this.api.get<Array<Message>>('/chat/' + channelId + '?limit=20').pipe(
      map((data) => this.mapToMessage(data))
    );
  }

  getOlderMessages(channelId: string, firstMessageId: any) {
    return this.api.get<Array<Message>>('/chat/' + channelId + '?limit=20&before=' + firstMessageId).pipe(
      map((data) => this.mapToMessage(data))
    );;
  }
  getMessagesAfter(channelId: string, lastMessageId: any) {
    return this.api.get<Array<Message>>('/chat/' + channelId + '?limit=20&after=' + lastMessageId).pipe(
      map((data) => this.mapToMessage(data))
    );;
  }

  addMessage(channelName : string, message : any) {
    this.api.post('/chat/' + channelName, message);
  }

  private mapToMessage(data : Array<any>) {
    return data.map((m) => m as Message);
  }
}
