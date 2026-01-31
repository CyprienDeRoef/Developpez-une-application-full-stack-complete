import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { FormFieldComponent } from './form-field.component';

describe('FormFieldComponent', () => {
  let component: FormFieldComponent;
  let fixture: ComponentFixture<FormFieldComponent>;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormFieldComponent],
      imports: [ReactiveFormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(FormFieldComponent);
    component = fixture.componentInstance;
    formBuilder = TestBed.inject(FormBuilder);

    component.formGroup = formBuilder.group({
      testField: ['', Validators.required],
    });
    component.controlName = 'testField';
    component.label = 'Test Field';

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('control getter', () => {
    it('should return the form control', () => {
      const control = component.control;
      expect(control).toBe(component.formGroup.get('testField'));
    });
  });

  describe('isInvalid getter', () => {
    it('should return true when control is invalid and touched', () => {
      const control = component.formGroup.get('testField')!;
      control.markAsTouched();
      control.setValue('');

      expect(component.isInvalid).toBe(true);
    });

    it('should return false when control is valid', () => {
      const control = component.formGroup.get('testField')!;
      control.markAsTouched();
      control.setValue('Valid value');

      expect(component.isInvalid).toBe(false);
    });

    it('should return false when control is invalid but not touched', () => {
      const control = component.formGroup.get('testField')!;
      control.setValue('');

      expect(component.isInvalid).toBe(false);
    });
  });

  describe('errorMessage getter', () => {
    it('should return required error message', () => {
      const control = component.formGroup.get('testField')!;
      control.markAsTouched();
      control.setValue('');

      expect(component.errorMessage).toBe('Ce champ est requis');
    });

    it('should return custom error message when provided', () => {
      component.errorMessages = { required: 'Custom required message' };
      const control = component.formGroup.get('testField')!;
      control.markAsTouched();
      control.setValue('');

      expect(component.errorMessage).toBe('Custom required message');
    });

    it('should return null when control is not touched', () => {
      const control = component.formGroup.get('testField')!;
      control.setValue('');

      expect(component.errorMessage).toBeNull();
    });
  });

  describe('fieldId getter', () => {
    it('should return controlName when id is not provided', () => {
      expect(component.fieldId).toBe('testField');
    });

    it('should return custom id when provided', () => {
      component.id = 'customId';
      expect(component.fieldId).toBe('customId');
    });
  });
});
