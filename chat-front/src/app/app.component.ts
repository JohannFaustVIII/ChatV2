import { Component, HostListener, OnInit } from '@angular/core';
import { IdleUserService } from './services/idle-user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'chat-front';

  constructor(private idleUserService : IdleUserService) {}

  ngOnInit(): void {
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler() {
    this.idleUserService.setOffline(); //TODO: think about it, as it is aborted when changing page
  }
}
