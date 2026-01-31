import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BackButtonComponent } from './back-button.component';

describe('BackButtonComponent', () => {
  let component: BackButtonComponent;
  let fixture: ComponentFixture<BackButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BackButtonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BackButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('onBackClick', () => {
    it('should emit backClicked event when onBackClick is called', () => {
      jest.spyOn(component.backClicked, 'emit');

      component.onBackClick();

      expect(component.backClicked.emit).toHaveBeenCalledWith();
    });

    it('should emit backClicked event only once', () => {
      jest.spyOn(component.backClicked, 'emit');

      component.onBackClick();

      expect(component.backClicked.emit).toHaveBeenCalledTimes(1);
    });
  });
});
