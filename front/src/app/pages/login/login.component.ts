import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../interfaces/auth.interface';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const loginRequest: LoginRequest = {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password,
      };

      this.authService.login(loginRequest).subscribe({
        next: (response) => {
          this.isLoading = false;
          // Navigate to feed page after successful login
          this.router.navigate(['/feed']);
        },
        error: (error) => {
          this.isLoading = false;

          // Display backend error message if available
          if (error.error?.message) {
            this.errorMessage = error.error.message;
          } else if (error.status === 400 || error.status === 401) {
            this.errorMessage = 'Email ou mot de passe incorrect';
          } else if (error.status === 0) {
            this.errorMessage =
              'Impossible de se connecter au serveur. Veuillez réessayer plus tard.';
          } else {
            this.errorMessage =
              'Une erreur est survenue lors de la connexion. Veuillez réessayer.';
          }
        },
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
