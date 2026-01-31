import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { ProfileComponent } from './profile.component';
import { AuthService } from '../../services/auth.service';
import { TopicService } from '../../services/topic.service';
import { User } from '../../interfaces/auth.interface';
import { Topic } from '../../interfaces/topic.interface';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let mockAuthService: jest.Mocked<AuthService>;
  let mockTopicService: jest.Mocked<TopicService>;

  const mockUser: User = {
    id: 1,
    name: 'Test User',
    email: 'test@example.com',
  };

  const mockTopics: Topic[] = [
    { id: 1, name: 'Topic 1', description: 'Description 1' },
  ];

  beforeEach(async () => {
    mockAuthService = {
      getCurrentUser: jest.fn(),
      updateProfile: jest.fn(),
    } as any;

    mockTopicService = {
      getUserSubscriptions: jest.fn(),
      unsubscribe: jest.fn(),
    } as any;

    await TestBed.configureTestingModule({
      declarations: [ProfileComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: mockAuthService },
        { provide: TopicService, useValue: mockTopicService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize form and load data', () => {
      mockAuthService.getCurrentUser.mockReturnValue(of(mockUser));
      mockTopicService.getUserSubscriptions.mockReturnValue(of(mockTopics));

      component.ngOnInit();

      expect(component.profileForm).toBeDefined();
      expect(mockAuthService.getCurrentUser).toHaveBeenCalled();
      expect(mockTopicService.getUserSubscriptions).toHaveBeenCalled();
    });

    it('should patch form with user data', () => {
      mockAuthService.getCurrentUser.mockReturnValue(of(mockUser));
      mockTopicService.getUserSubscriptions.mockReturnValue(of(mockTopics));

      component.ngOnInit();

      expect(component.profileForm.value.name).toBe(mockUser.name);
      expect(component.profileForm.value.email).toBe(mockUser.email);
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      mockAuthService.getCurrentUser.mockReturnValue(of(mockUser));
      mockTopicService.getUserSubscriptions.mockReturnValue(of(mockTopics));
      component.ngOnInit();
      component.profileForm.patchValue({
        name: 'Updated Name',
        email: 'updated@example.com',
      });
    });

    it('should call updateProfile with form values', () => {
      mockAuthService.updateProfile.mockReturnValue(of(mockUser));

      component.onSubmit();

      expect(mockAuthService.updateProfile).toHaveBeenCalledWith({
        name: 'Updated Name',
        email: 'updated@example.com',
      });
    });

    it('should set success message on successful update', () => {
      mockAuthService.updateProfile.mockReturnValue(of(mockUser));

      component.onSubmit();

      expect(component.successMessage).toBe('Profil mis à jour avec succès');
    });

    it('should set error message on failure', () => {
      mockAuthService.updateProfile.mockReturnValue(
        throwError(() => ({ error: { message: 'Error' } })),
      );

      component.onSubmit();

      expect(component.error).toBeTruthy();
    });

    it('should not submit when form is invalid', () => {
      component.profileForm.patchValue({ name: '', email: '' });

      component.onSubmit();

      expect(mockAuthService.updateProfile).not.toHaveBeenCalled();
    });

    it('should include password in update if provided', () => {
      component.profileForm.patchValue({
        name: 'Updated Name',
        email: 'updated@example.com',
        password: 'newpassword',
      });
      mockAuthService.updateProfile.mockReturnValue(of(mockUser));

      component.onSubmit();

      expect(mockAuthService.updateProfile).toHaveBeenCalledWith({
        name: 'Updated Name',
        email: 'updated@example.com',
        password: 'newpassword',
      });
    });
  });

  describe('loadSubscriptions', () => {
    it('should load user subscriptions', () => {
      mockAuthService.getCurrentUser.mockReturnValue(of(mockUser));
      mockTopicService.getUserSubscriptions.mockReturnValue(of(mockTopics));

      component.ngOnInit();

      expect(component.subscriptions).toEqual(mockTopics);
    });
  });
});
