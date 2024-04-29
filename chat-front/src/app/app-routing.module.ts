import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WelcomeComponent } from './welcome/welcome.component';
import { ChannelSingleComponent } from './channel-single/channel-single.component';
import { AuthGuard } from './guard/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: WelcomeComponent, canActivate: [AuthGuard]
  },
  {
    path: 'c/:id',
    component: ChannelSingleComponent, canActivate: [AuthGuard]
  },
  { 
    path: '**', 
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
