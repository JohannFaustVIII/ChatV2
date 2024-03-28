import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-channel-single',
  templateUrl: './channel-single.component.html',
  styleUrls: ['./channel-single.component.css']
})
export class ChannelSingleComponent {
  id = '';
  title = '';

  constructor(private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(value => {
      const _id = value.get('id');
      this.id = _id !== null ? _id : '';
    })
  }

}
