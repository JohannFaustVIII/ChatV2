import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent {

  @Input() message: any; 

  getTime() : string {
    var result = '';
    result += this.message.serverTime;
    if (this.message.editTime != null) {
      result += ' [ edited:' + this.message.editTime + ' ]';
    }
    return result;
  }

}
