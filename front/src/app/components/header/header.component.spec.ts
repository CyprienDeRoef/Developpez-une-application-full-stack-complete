import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { HeaderComponent } from './header.component';
import { AuthService } from '../../services/auth.service';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let mockAuthService: jest.Mocked<AuthService>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    mockAuthService = {
      isAuthenticated: jest.fn(),
      logout: jest.fn(),
    } as any;

    mockRouter = {
      navigate: jest.fn(),
    } as any;

    await TestBed.configureTestingModule({
      declarations: [HeaderComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('isAuthenticated', () => {
    it('should call authService.isAuthenticated', () => {
      mockAuthService.isAuthenticated.mockReturnValue(true);

      const result = component.isAuthenticated();

      expect(mockAuthService.isAuthenticated).toHaveBeenCalled();
      expect(result).toBe(true);
    });

    it('should return false when not authenticated', () => {
      mockAuthService.isAuthenticated.mockReturnValue(false);

      const result = component.isAuthenticated();

      expect(result).toBe(false);
    });
  });

  describe('logout', () => {
    it('should call authService.logout and navigate to home', () => {
      component.logout();

      expect(mockAuthService.logout).toHaveBeenCalled();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
    });

    it('should close sidebar after logout', () => {
      component.isSidebarOpen = true;

      component.logout();

      expect(component.isSidebarOpen).toBe(false);
    });
  });

  describe('toggleSidebar', () => {
    it('should toggle sidebar state from false to true', () => {
      component.isSidebarOpen = false;

      component.toggleSidebar();

      expect(component.isSidebarOpen).toBe(true);
    });

    it('should toggle sidebar state from true to false', () => {
      component.isSidebarOpen = true;

      component.toggleSidebar();

      expect(component.isSidebarOpen).toBe(false);
    });
  });

  describe('closeSidebar', () => {
    it('should set isSidebarOpen to false', () => {
      component.isSidebarOpen = true;

      component.closeSidebar();

      expect(component.isSidebarOpen).toBe(false);
    });
  });
});
