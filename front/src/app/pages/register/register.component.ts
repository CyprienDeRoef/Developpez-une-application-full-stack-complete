import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../interfaces/auth.interface';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const registerRequest: RegisterRequest = {
        name: this.registerForm.value.name,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
      };

      this.authService.register(registerRequest).subscribe({
        next: (response) => {
          console.log('Registration successful:', response);
          this.isLoading = false;
          // Navigate to home page after successful registration
          this.router.navigate(['/']);
        },
        error: (error) => {
          console.error('Registration error:', error);
          this.isLoading = false;
          if (error.status === 400) {
            this.errorMessage =
              'Registration failed. Email may already be in use.';
          } else {
            this.errorMessage =
              'An error occurred during registration. Please try again.';
          }
        },
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
