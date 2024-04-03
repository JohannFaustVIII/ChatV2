import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  messages : {[id : string] : any} = {
    'a-b-c-d': [
        {id : 'A1', sender: 'A1-User', message : 'Hello'},
        {id : 'A2', sender: 'A2-User', message : 'Hello'},
        {id : 'A3', sender: 'A3-User', message : 'Hello'}
      ],
    'e-f-g-h' : [
        {id : 'E1', sender: 'E1-User', message : 'Hello World'},
        {id : 'E2', sender: 'E2-User', message : 'Do not talk like that'},
        {id : 'E3', sender: 'E3-User', message : 'Please, don\'t argue'}
      ]
  };

  constructor() { }

  getMessages(channelName : string) {
    return this.messages[channelName];
  }

  addMessage(channelName : string, message : any) {
    this.messages[channelName].push(message);
  }
}
