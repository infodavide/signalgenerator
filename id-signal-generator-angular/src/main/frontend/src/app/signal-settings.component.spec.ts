import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SignalSettingsComponent } from './signal-settings.component';

describe('SignalSettingsComponent', () => {
  let component: SignalSettingsComponent;
  let fixture: ComponentFixture<SignalSettingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SignalSettingsComponent]
    });
    fixture = TestBed.createComponent(SignalSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
