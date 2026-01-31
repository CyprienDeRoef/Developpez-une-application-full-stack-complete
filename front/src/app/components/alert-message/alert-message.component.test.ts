import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AlertMessageComponent } from './alert-message.component';

describe('AlertMessageComponent', () => {
  let component: AlertMessageComponent;
  let fixture: ComponentFixture<AlertMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AlertMessageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AlertMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('onDismiss', () => {
    it('should emit dismissed event when onDismiss is called', () => {
      jest.spyOn(component.dismissed, 'emit');

      component.onDismiss();

      expect(component.dismissed.emit).toHaveBeenCalledWith();
    });

    it('should emit dismissed event exactly once', () => {
      jest.spyOn(component.dismissed, 'emit');

      component.onDismiss();

      expect(component.dismissed.emit).toHaveBeenCalledTimes(1);
    });
  });
});
