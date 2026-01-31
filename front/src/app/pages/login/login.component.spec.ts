import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: jest.Mocked<AuthService>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    mockAuthService = {
      login: jest.fn(),
    } as any;

    mockRouter = {
      navigate: jest.fn(),
    } as any;

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize form with validators', () => {
      expect(component.loginForm).toBeDefined();
      expect(component.loginForm.get('email')).toBeDefined();
      expect(component.loginForm.get('password')).toBeDefined();
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      component.loginForm.patchValue({
        email: 'test@example.com',
        password: 'password123',
      });
    });

    it('should call authService.login with form values', () => {
      mockAuthService.login.mockReturnValue(of({ token: 'test-token' } as any));

      component.onSubmit();

      expect(mockAuthService.login).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123',
      });
    });

    it('should navigate to feed on successful login', () => {
      mockAuthService.login.mockReturnValue(of({ token: 'test-token' } as any));

      component.onSubmit();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/feed']);
    });

    it('should set error message on login failure', () => {
      mockAuthService.login.mockReturnValue(
        throwError(() => ({ status: 401, error: {} })),
      );

      component.onSubmit();

      expect(component.errorMessage).toBe('Email ou mot de passe incorrect');
    });

    it('should not submit when form is invalid', () => {
      component.loginForm.patchValue({ email: '', password: '' });

      component.onSubmit();

      expect(mockAuthService.login).not.toHaveBeenCalled();
    });

    it('should set loading state during submission', () => {
      mockAuthService.login.mockReturnValue(of({ token: 'test-token' } as any));

      component.onSubmit();

      expect(component.isLoading).toBe(false);
    });
  });

  describe('goBack', () => {
    it('should navigate to home', () => {
      component.goBack();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
    });
  });
});
