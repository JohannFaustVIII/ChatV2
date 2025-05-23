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
    this.initHook();
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
    this.updateState();
    this.activityLoop = setInterval(() => {
      this.updateState();
    }, 60000);
  }

  private reset(): any {
    clearTimeout(this.timeoutId);
    const oldState = this.isOnline;
    this.isOnline = true;
    if (!oldState) {
      this.updateState();
    }
    this.startIdleTimer();
  }

  private startIdleTimer() {
    this.timeoutId = setTimeout(() => {
      this.isOnline = false;
      this.updateState();
      console.log("You are AFK");
    }, IdleTimes.IdleTime);
  }

  private initHook() {
    this.userService.setHook();
  }

  private updateState() {
    if (this.isOnline) {
      this.userService.setOnline();
    } else if (!this.isOffline) {
      this.userService.setAfk();
    } else {
      this.userService.setOffline();
    }
  }
}
