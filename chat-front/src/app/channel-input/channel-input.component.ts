import { Component } from '@angular/core';
import { ChannelService } from '../services/channel.service';

@Component({
  selector: 'app-channel-input',
  templateUrl: './channel-input.component.html',
  styleUrl: './channel-input.component.css'
})
export class ChannelInputComponent {

  new_channel : string = "";

  constructor(private channelService: ChannelService) {
  }

  addChannel() {
    this.channelService.addChannel(this.new_channel);
    this.new_channel = "";
  }
}
