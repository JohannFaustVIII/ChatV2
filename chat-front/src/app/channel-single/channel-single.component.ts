import { ChangeDetectorRef, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from '../services/message.service';
import { Listener } from '../models/listener';
import { SseService } from '../services/sse.service';

@Component({
  selector: 'app-channel-single',
  templateUrl: './channel-single.component.html',
  styleUrls: ['./channel-single.component.css']
})
export class ChannelSingleComponent extends Listener {

  id = '';
  title = '';

  messages : Array<any> = []
  private interval : any;

  constructor(private route: ActivatedRoute, private messageService: MessageService, private sse : SseService, private changeDetector : ChangeDetectorRef) {
    super();
    this.messageService = messageService;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(value => {
      const _id = value.get('id');
      this.id = _id !== null ? _id : '';
      if (_id !== null) {
        this.sse.addListener(_id, this);
        this.getMessages();
      }
    })
  }

  ngAfterViewInit(): void {
    this.getMessages();
  }

  override notify(): void {
    this.getMessages();
  }
  
  getMessages() {
    this.messageService.getMessages(this.id).subscribe(data => {
      this.messages = data;
      this.changeDetector.detectChanges();
    });
  }

}
