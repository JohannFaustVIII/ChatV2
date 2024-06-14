import { Component, HostListener, OnInit } from '@angular/core';
import { UserService } from './services/user.service';
import { IdleUserService } from './services/idle-user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'chat-front';

  constructor(private userService : UserService, private idleUserService : IdleUserService) {}

  ngOnInit(): void {
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler() {
    this.userService.setOffline(); //TODO: think about it, as it is aborted when changing page
  }
}
