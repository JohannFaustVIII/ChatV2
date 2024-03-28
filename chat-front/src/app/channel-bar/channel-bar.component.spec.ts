import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelBarComponent } from './channel-bar.component';

describe('ChannelBarComponent', () => {
  let component: ChannelBarComponent;
  let fixture: ComponentFixture<ChannelBarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChannelBarComponent]
    });
    fixture = TestBed.createComponent(ChannelBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
