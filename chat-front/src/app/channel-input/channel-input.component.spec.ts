import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelInputComponent } from './channel-input.component';

describe('ChannelInputComponent', () => {
  let component: ChannelInputComponent;
  let fixture: ComponentFixture<ChannelInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChannelInputComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChannelInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
