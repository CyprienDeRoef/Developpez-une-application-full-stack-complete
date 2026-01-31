import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormHeaderComponent } from './form-header.component';

describe('FormHeaderComponent', () => {
  let component: FormHeaderComponent;
  let fixture: ComponentFixture<FormHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormHeaderComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FormHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onBackClick', () => {
    it('should emit backClicked event when onBackClick is called', () => {
      const emitSpy = jest.spyOn(component.backClicked, 'emit');

      component.onBackClick();

      expect(emitSpy).toHaveBeenCalledWith();
    });

    it('should emit backClicked event exactly once', () => {
      const emitSpy = jest.spyOn(component.backClicked, 'emit');

      component.onBackClick();

      expect(emitSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('Input properties', () => {
    it('should have default values', () => {
      expect(component.title).toBe('');
      expect(component.showBackButton).toBe(true);
      expect(component.centered).toBe(false);
    });

    it('should accept custom title', () => {
      component.title = 'Custom Title';
      fixture.detectChanges();

      expect(component.title).toBe('Custom Title');
    });

    it('should accept showBackButton input', () => {
      component.showBackButton = false;
      fixture.detectChanges();

      expect(component.showBackButton).toBe(false);
    });

    it('should accept centered input', () => {
      component.centered = true;
      fixture.detectChanges();

      expect(component.centered).toBe(true);
    });
  });
});
