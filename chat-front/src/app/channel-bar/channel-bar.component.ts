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
  }

  override notify(): void {
    this.getChannels();
  }

  getChannels() {
    this.channelService.getChannels().subscribe(data => {
      this.channels = data;
      this.changeDetector.detectChanges();
    });
  }

}
