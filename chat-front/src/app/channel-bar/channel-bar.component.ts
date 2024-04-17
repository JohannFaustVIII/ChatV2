import { ChangeDetectorRef, Component } from '@angular/core';
import { ChannelService } from '../services/channel.service';
import { Listener } from '../models/listener';
import { SseService } from '../services/sse.service';

@Component({
  selector: 'app-channel-bar',
  templateUrl: './channel-bar.component.html',
  styleUrls: ['./channel-bar.component.css']
})
export class ChannelBarComponent extends Listener {

  channels : Array<any> = []

  private interval : any;

  constructor(private channelService : ChannelService, private sse : SseService, private changeDetector : ChangeDetectorRef) {
    super();
    sse.addListener('channel', this);
  }

  ngAfterViewInit(): void {
    this.getChannels();
    // TODO: is it fine to do it like that? maybe backend should emit an event that something has changed?
    // maybe open a stream of events to notify front about change on backend?
  }

  override notify(): void {
    console.log('Notified bar');
    this.getChannels();
  }

  getChannels() {
    this.channelService.getChannels().subscribe(data => {
      this.channels = data;
      this.changeDetector.detectChanges();
    });
  }

}
