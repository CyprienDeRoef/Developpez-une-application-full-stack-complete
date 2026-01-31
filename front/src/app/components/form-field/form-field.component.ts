import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-form-field',
  templateUrl: './form-field.component.html',
  styleUrls: ['./form-field.component.scss'],
})
export class FormFieldComponent {
  @Input() formGroup!: FormGroup;
  @Input() controlName!: string;
  @Input() label!: string;
  @Input() type: string = 'text';
  @Input() placeholder: string = '';
  @Input() id?: string;
  @Input() errorMessages?: { [key: string]: string };

  get control() {
    return this.formGroup.get(this.controlName);
  }

  get isInvalid(): boolean {
    return !!(this.control?.invalid && this.control?.touched);
  }

  get errorMessage(): string | null {
    if (!this.control || !this.control.errors || !this.control.touched) {
      return null;
    }

    const errors = this.control.errors;

    if (this.errorMessages) {
      for (const key in errors) {
        if (this.errorMessages[key]) {
          return this.errorMessages[key];
        }
      }
    }

    // Default error messages
    if (errors['required']) {
      return 'Ce champ est requis';
    }
    if (errors['email']) {
      return 'Veuillez entrer un email valide';
    }
    if (errors['minlength']) {
      return `Minimum ${errors['minlength'].requiredLength} caractères requis`;
    }
    if (errors['maxlength']) {
      return `Maximum ${errors['maxlength'].requiredLength} caractères autorisés`;
    }

    return 'Valeur invalide';
  }

  get fieldId(): string {
    return this.id || this.controlName;
  }
}
