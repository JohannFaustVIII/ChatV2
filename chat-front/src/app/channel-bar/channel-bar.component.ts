import { Component } from '@angular/core';
import { ChannelService } from '../services/channel.service';

@Component({
  selector: 'app-channel-bar',
  templateUrl: './channel-bar.component.html',
  styleUrls: ['./channel-bar.component.css']
})
export class ChannelBarComponent {

  channels : Array<any> = []

  private interval : any;

  constructor(private channelService : ChannelService) {
  }

  ngAfterViewInit(): void {
    this.getChannels();
    // TODO: is it fine to do it like that? maybe backend should emit an event that something has changed?
    // maybe open a stream of events to notify front about change on backend?
    this.interval = setInterval(() => this.getChannels(), 1000);
  }

  getChannels() {
    this.channelService.getChannels().subscribe(data => this.channels = data);
  }
}
