import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.scss'],
})
export class CommentFormComponent {
  @Input() commentForm!: FormGroup;
  @Input() isSubmitting: boolean = false;
  @Input() errorMessage: string | null = null;
  @Output() formSubmitted = new EventEmitter<void>();

  onSubmit(): void {
    if (this.commentForm.valid && !this.isSubmitting) {
      this.formSubmitted.emit();
    }
  }
}
