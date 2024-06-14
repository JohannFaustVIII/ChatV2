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

  userInactive: Subject<boolean> = new Subject();

  constructor(private userService : UserService) { 
    this.initListeners();
  }

  initListeners() {
    window.addEventListener('mousemove', () => this.reset());
    window.addEventListener('click', () => this.reset());
    window.addEventListener('keypress', () => this.reset());
    window.addEventListener('DOMMouseScroll', () => this.reset());
    window.addEventListener('mousewheel', () => this.reset());
    window.addEventListener('touchmove', () => this.reset());
    window.addEventListener('MSPointerMove', () => this.reset());
  }

  reset(): any {
    clearTimeout(this.timeoutId);
    this.userService.setOnline(); // TODO: TO FIX, it sends a lot of requests about being online 
    this.startIdleTimer();
  }

  startIdleTimer() {
    this.timeoutId = setTimeout(() => {
      // send AFK status?
      console.log("You are AFK");
    }, IdleTimes.IdleTime);
  }
}
