import { Component } from '@angular/core';
import { UserService } from '../services/user.service';
import { UserDetails } from '../models/userDetails.model';
import { SseService } from '../services/sse.service';
import { Listener } from '../models/listener';

@Component({
  selector: 'app-user-container',
  templateUrl: './user-container.component.html',
  styleUrl: './user-container.component.css'
})
export class UserContainerComponent extends Listener{

  onlineUsers : Array<UserDetails> = []
  afkUsers : Array<UserDetails> = []
  offlineUsers : Array<UserDetails> = []

  constructor(private userService: UserService, private sseService : SseService) {
    super();
  }

  override notify(): void {
    this.getUsers();
  }

  ngOnInit() : void {
    this.getUsers();
    this.sseService.addListener('users', this);
  }

  getUsers() {
    this.userService.getUsers().subscribe(data => {
      this.onlineUsers = data['ONLINE'];
      this.afkUsers = data['AFK'];
      this.offlineUsers = data['OFFLINE'];
    });
  }

}
