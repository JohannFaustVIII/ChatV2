import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-channel-single',
  templateUrl: './channel-single.component.html',
  styleUrls: ['./channel-single.component.css']
})
export class ChannelSingleComponent {
  id = '';
  title = '';

  messages : Array<any> = []
  private interval : any;

  constructor(private route: ActivatedRoute, private messageService: MessageService) {
    this.messageService = messageService;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(value => {
      const _id = value.get('id');
      this.id = _id !== null ? _id : '';
    })
  }

  ngAfterViewInit(): void {
    this.getMessages();
    this.interval = setInterval(() => this.getMessages(), 1000);
  }

  getMessages() {
    this.messageService.getMessages(this.id).subscribe(data => this.messages = data);
  }

}
