import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { CommentFormComponent } from './comment-form.component';

describe('CommentFormComponent', () => {
  let component: CommentFormComponent;
  let fixture: ComponentFixture<CommentFormComponent>;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommentFormComponent],
      imports: [ReactiveFormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentFormComponent);
    component = fixture.componentInstance;
    formBuilder = TestBed.inject(FormBuilder);

    component.commentForm = formBuilder.group({
      content: [''],
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onSubmit', () => {
    it('should emit formSubmitted event when form is valid and not submitting', () => {
      component.commentForm.setValue({ content: 'Test comment' });
      component.isSubmitting = false;
      const emitSpy = jest.spyOn(component.formSubmitted, 'emit');

      component.onSubmit();

      expect(emitSpy).toHaveBeenCalledWith();
    });

    it('should not emit formSubmitted event when form is invalid', () => {
      component.commentForm.setErrors({ invalid: true });
      const emitSpy = jest.spyOn(component.formSubmitted, 'emit');

      component.onSubmit();

      expect(emitSpy).not.toHaveBeenCalled();
    });

    it('should not emit formSubmitted event when isSubmitting is true', () => {
      component.commentForm.setValue({ content: 'Test comment' });
      component.isSubmitting = true;
      const emitSpy = jest.spyOn(component.formSubmitted, 'emit');

      component.onSubmit();

      expect(emitSpy).not.toHaveBeenCalled();
    });
  });
});
