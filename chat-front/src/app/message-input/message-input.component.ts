import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-message-input',
  templateUrl: './message-input.component.html',
  styleUrls: ['./message-input.component.css']
})
export class MessageInputComponent {

  id : string = '';
  
  new_message : string = '';

  constructor(private route: ActivatedRoute, private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(value => {
      const _id = value.get('id');
      this.id = _id !== null ? _id : '';
    })
  }

  sendMessage() : void {
    this.messageService.addMessage(this.id, this.new_message);
    this.new_message = '';
  }
}
