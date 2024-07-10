import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ChannelBarComponent } from './channel-bar/channel-bar.component';
import { ChannelSingleComponent } from './channel-single/channel-single.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MessageComponent } from './message/message.component';
import { MessageInputComponent } from './message-input/message-input.component'; 
import { HttpClientModule } from '@angular/common/http';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ChannelInputComponent } from './channel-input/channel-input.component';
import { UserContainerComponent } from './user-container/user-container.component';
import { UserListComponent } from './user-list/user-list.component';

function initializeKeycloak(keycloak : KeycloakService) {
  return () => keycloak.init({
    config: {
      url: 'http://localhost:8180',
      realm: 'ChatV2Realm',
      clientId: 'cv2-frontend'
    },
    initOptions: {
      checkLoginIframe: false
    },
    enableBearerInterceptor: true,
    bearerPrefix: 'Bearer'
  })
}

@NgModule({
  declarations: [
    AppComponent,
    ChannelBarComponent,
    ChannelInputComponent,
    ChannelSingleComponent,
    WelcomeComponent,
    MessageComponent,
    MessageInputComponent,
    UserContainerComponent,
    UserListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    InfiniteScrollModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatInputModule,
    MatFormFieldModule,
    KeycloakAngularModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
