import { Component, Input } from '@angular/core';
import { UserDetails } from '../models/userDetails.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent {

  @Input() title: string = '';
  @Input() users: UserDetails[] = [];  

}
