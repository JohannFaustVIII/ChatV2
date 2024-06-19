import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { UserService } from './user.service';

export enum IdleTimes {
  IdleTime = 10_000
}

@Injectable({
  providedIn: 'root'
})
export class IdleUserService {

  private timeoutId: any;
  private isOnline: boolean = true;
  private isOffline: boolean = false;
  private activityLoop: any;

  userInactive: Subject<boolean> = new Subject();

  constructor(private userService : UserService) { 
    this.initListeners();
    this.initActivityLoop();
  }

  setOffline() {
    this.isOffline = true;
    this.userService.setOffline();
  }

  private initListeners() {
    window.addEventListener('mousemove', () => this.reset());
    window.addEventListener('click', () => this.reset());
    window.addEventListener('keypress', () => this.reset());
    window.addEventListener('DOMMouseScroll', () => this.reset());
    window.addEventListener('mousewheel', () => this.reset());
    window.addEventListener('touchmove', () => this.reset());
    window.addEventListener('MSPointerMove', () => this.reset());
  }

  private initActivityLoop() {
    this.activityLoop = setInterval(() => {
      if (this.isOnline) {
        this.userService.setOnline();
      } else if (!this.isOffline) {
        this.userService.setAfk();
      } else {
        this.userService.setOffline();
      }
    }, 60000);
  }

  private reset(): any {
    clearTimeout(this.timeoutId);
    this.isOnline = true;
    this.startIdleTimer();
  }

  private startIdleTimer() {
    this.timeoutId = setTimeout(() => {
      this.isOnline = false;
      console.log("You are AFK");
    }, IdleTimes.IdleTime);
  }
}
