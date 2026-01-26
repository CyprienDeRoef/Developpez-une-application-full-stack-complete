import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import {
  RegisterRequest,
  LoginRequest,
  JwtResponse,
  User,
} from '../interfaces/auth.interface';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private pathService = 'api/auth';

  constructor(private httpClient: HttpClient) {}

  /**
   * Register a new user
   * @param registerRequest Registration data
   * @returns Observable with JWT response
   */
  public register(registerRequest: RegisterRequest): Observable<JwtResponse> {
    return this.httpClient
      .post<JwtResponse>(`${this.pathService}/register`, registerRequest)
      .pipe(
        tap((response) => {
          // Store the JWT token in localStorage
          if (response.token) {
            localStorage.setItem('token', response.token);
            localStorage.setItem('userId', response.id.toString());
          }
        }),
      );
  }

  /**
   * Login an existing user
   * @param loginRequest Login credentials
   * @returns Observable with JWT response
   */
  public login(loginRequest: LoginRequest): Observable<JwtResponse> {
    return this.httpClient
      .post<JwtResponse>(`${this.pathService}/login`, loginRequest)
      .pipe(
        tap((response) => {
          // Store the JWT token in localStorage
          if (response.token) {
            localStorage.setItem('token', response.token);
            localStorage.setItem('userId', response.id.toString());
          }
        }),
      );
  }

  /**
   * Get current authenticated user
   * @returns Observable with user data
   */
  public getCurrentUser(): Observable<User> {
    return this.httpClient.get<User>(`${this.pathService}/me`);
  }

  /**
   * Logout the current user
   */
  public logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
  }

  /**
   * Check if user is authenticated
   * @returns true if user has a valid token
   */
  public isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  /**
   * Get the stored JWT token
   * @returns The JWT token or null
   */
  public getToken(): string | null {
    return localStorage.getItem('token');
  }
}
