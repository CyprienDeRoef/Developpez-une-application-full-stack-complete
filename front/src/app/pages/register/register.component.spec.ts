import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: jest.Mocked<AuthService>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    mockAuthService = {
      register: jest.fn(),
    } as any;

    mockRouter = {
      navigate: jest.fn(),
    } as any;

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize form with validators', () => {
      expect(component.registerForm).toBeDefined();
      expect(component.registerForm.get('name')).toBeDefined();
      expect(component.registerForm.get('email')).toBeDefined();
      expect(component.registerForm.get('password')).toBeDefined();
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      component.registerForm.patchValue({
        name: 'Test User',
        email: 'test@example.com',
        password: 'password123',
      });
    });

    it('should call authService.register with form values', () => {
      mockAuthService.register.mockReturnValue(
        of({ token: 'test-token' } as any),
      );

      component.onSubmit();

      expect(mockAuthService.register).toHaveBeenCalledWith({
        name: 'Test User',
        email: 'test@example.com',
        password: 'password123',
      });
    });

    it('should navigate to home on successful registration', () => {
      mockAuthService.register.mockReturnValue(
        of({ token: 'test-token' } as any),
      );

      component.onSubmit();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
    });

    it('should set error message on registration failure', () => {
      mockAuthService.register.mockReturnValue(
        throwError(() => ({ status: 400 })),
      );

      component.onSubmit();

      expect(component.errorMessage).toBe(
        'Registration failed. Email may already be in use.',
      );
    });

    it('should not submit when form is invalid', () => {
      component.registerForm.patchValue({ name: '', email: '', password: '' });

      component.onSubmit();

      expect(mockAuthService.register).not.toHaveBeenCalled();
    });

    it('should set loading state during submission', () => {
      mockAuthService.register.mockReturnValue(
        of({ token: 'test-token' } as any),
      );

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
