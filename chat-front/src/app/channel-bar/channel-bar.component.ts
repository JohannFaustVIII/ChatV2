import { Component } from '@angular/core';
import { ChannelService } from '../services/channel.service';

@Component({
  selector: 'app-channel-bar',
  templateUrl: './channel-bar.component.html',
  styleUrls: ['./channel-bar.component.css']
})
export class ChannelBarComponent {

  channels : Array<any> = []

  constructor(private channelService : ChannelService) {
    this.channels = channelService.getChannels()
  }

}
