import { Component } from '@angular/core';

@Component({
  selector: 'app-channel-bar',
  templateUrl: './channel-bar.component.html',
  styleUrls: ['./channel-bar.component.css']
})
export class ChannelBarComponent {

  channels = [
    {
      id: "a-b-c-d",
      name: "Channel 1"
    },
    {
      id: "e-f-g-h",
      name: "Channel 2"
    }
  ]

}
