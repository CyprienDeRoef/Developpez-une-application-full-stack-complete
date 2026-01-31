import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CreatePostComponent } from './create-post.component';
import { PostService } from '../../services/post.service';
import { TopicService } from '../../services/topic.service';
import { Topic } from '../../interfaces/topic.interface';

describe('CreatePostComponent', () => {
  let component: CreatePostComponent;
  let fixture: ComponentFixture<CreatePostComponent>;
  let mockPostService: jest.Mocked<PostService>;
  let mockTopicService: jest.Mocked<TopicService>;
  let mockRouter: jest.Mocked<Router>;

  const mockTopics: Topic[] = [
    { id: 1, name: 'Topic 1', description: 'Description 1' },
    { id: 2, name: 'Topic 2', description: 'Description 2' },
  ];

  beforeEach(async () => {
    mockPostService = {
      create: jest.fn(),
    } as any;

    mockTopicService = {
      all: jest.fn(),
    } as any;

    mockRouter = {
      navigate: jest.fn(),
    } as any;

    await TestBed.configureTestingModule({
      declarations: [CreatePostComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: PostService, useValue: mockPostService },
        { provide: TopicService, useValue: mockTopicService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CreatePostComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize form with validators', () => {
      mockTopicService.all.mockReturnValue(of(mockTopics));

      component.ngOnInit();

      expect(component.postForm).toBeDefined();
      expect(component.postForm.get('title')).toBeDefined();
      expect(component.postForm.get('content')).toBeDefined();
      expect(component.postForm.get('topicId')).toBeDefined();
    });

    it('should load topics on init', () => {
      mockTopicService.all.mockReturnValue(of(mockTopics));

      component.ngOnInit();

      expect(mockTopicService.all).toHaveBeenCalled();
      expect(component.topics).toEqual(mockTopics);
    });
  });

  describe('loadTopics', () => {
    it('should set errorMessage on error', () => {
      mockTopicService.all.mockReturnValue(
        throwError(() => new Error('Error')),
      );

      component.ngOnInit();

      expect(component.errorMessage).toBe('Impossible de charger les thèmes');
    });
  });

  describe('onTopicAdded', () => {
    it('should add topic to selectedTopics', () => {
      mockTopicService.all.mockReturnValue(of(mockTopics));
      component.ngOnInit();
      component.topics = mockTopics;
      component.selectedTopics = [];

      component.onTopicAdded(1);

      expect(component.selectedTopics).toEqual([mockTopics[0]]);
    });

    it('should not add duplicate topic', () => {
      mockTopicService.all.mockReturnValue(of(mockTopics));
      component.ngOnInit();
      component.topics = mockTopics;
      component.selectedTopics = [mockTopics[0]];

      component.onTopicAdded(1);

      expect(component.selectedTopics.length).toBe(1);
    });
  });

  describe('onTopicRemoved', () => {
    it('should remove topic from selectedTopics', () => {
      component.selectedTopics = [mockTopics[0], mockTopics[1]];

      component.onTopicRemoved(mockTopics[0]);

      expect(component.selectedTopics).toEqual([mockTopics[1]]);
    });
  });

  describe('goBack', () => {
    it('should navigate to feed', () => {
      component.goBack();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/feed']);
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      mockTopicService.all.mockReturnValue(of(mockTopics));
      component.ngOnInit();
      component.postForm.patchValue({
        title: 'Test Title',
        content: 'Test Content',
      });
      component.selectedTopics = [mockTopics[0]];
    });

    it('should create post when form is valid and has topics', () => {
      const mockPost = { id: 1, title: 'Test Title', content: 'Test Content' };
      mockPostService.create.mockReturnValue(of(mockPost as any));

      component.onSubmit();

      expect(mockPostService.create).toHaveBeenCalledWith({
        title: 'Test Title',
        content: 'Test Content',
        topicIds: [1],
      });
    });

    it('should navigate to feed after successful creation', () => {
      const mockPost = { id: 1, title: 'Test Title', content: 'Test Content' };
      mockPostService.create.mockReturnValue(of(mockPost as any));

      component.onSubmit();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/feed']);
    });

    it('should not submit when form is invalid', () => {
      component.postForm.patchValue({ title: '', content: '' });

      component.onSubmit();

      expect(mockPostService.create).not.toHaveBeenCalled();
    });

    it('should not submit when no topics selected', () => {
      component.selectedTopics = [];

      component.onSubmit();

      expect(mockPostService.create).not.toHaveBeenCalled();
    });
  });
});
