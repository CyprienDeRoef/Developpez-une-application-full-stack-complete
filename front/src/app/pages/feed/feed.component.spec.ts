import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FeedComponent } from './feed.component';
import { PostService } from '../../services/post.service';
import { TopicService } from '../../services/topic.service';
import { Post } from '../../interfaces/post.interface';
import { Topic } from '../../interfaces/topic.interface';

describe('FeedComponent', () => {
  let component: FeedComponent;
  let fixture: ComponentFixture<FeedComponent>;
  let mockPostService: jest.Mocked<PostService>;
  let mockTopicService: jest.Mocked<TopicService>;
  let mockRouter: jest.Mocked<Router>;

  const mockPosts: Post[] = [
    {
      id: 1,
      title: 'Post 1',
      content: 'Content 1',
      authorName: 'Author 1',
      authorId: 1,
      topicNames: ['Topic 1'],
      topicIds: [1],
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-01'),
    },
    {
      id: 2,
      title: 'Post 2',
      content: 'Content 2',
      authorName: 'Author 2',
      authorId: 1,
      topicNames: ['Topic 2'],
      topicIds: [1],
      createdAt: new Date('2024-01-02'),
      updatedAt: new Date('2024-01-01'),
    },
  ];

  const mockTopics: Topic[] = [
    { id: 1, name: 'Topic 1', description: 'Description 1' },
  ];

  beforeEach(async () => {
    mockPostService = {
      all: jest.fn(),
    } as any;

    mockTopicService = {
      getUserSubscriptions: jest.fn(),
    } as any;

    mockRouter = {
      navigate: jest.fn(),
    } as any;

    await TestBed.configureTestingModule({
      declarations: [FeedComponent],
      providers: [
        { provide: PostService, useValue: mockPostService },
        { provide: TopicService, useValue: mockTopicService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FeedComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load data on init', () => {
      mockPostService.all.mockReturnValue(of(mockPosts));
      mockTopicService.getUserSubscriptions.mockReturnValue(of(mockTopics));

      component.ngOnInit();

      expect(mockPostService.all).toHaveBeenCalled();
      expect(mockTopicService.getUserSubscriptions).toHaveBeenCalled();
    });
  });

  describe('loadData', () => {
    it('should set posts and subscriptions', () => {
      mockPostService.all.mockReturnValue(of(mockPosts));
      mockTopicService.getUserSubscriptions.mockReturnValue(of(mockTopics));

      component.loadData();

      expect(component.allPosts).toEqual(mockPosts);
      expect(component.subscribedTopics).toEqual(mockTopics);
      expect(component.isLoading).toBe(false);
    });

    it('should handle error', () => {
      mockPostService.all.mockReturnValue(throwError(() => new Error('Error')));
      mockTopicService.getUserSubscriptions.mockReturnValue(of(mockTopics));

      component.loadData();

      expect(component.isLoading).toBe(false);
    });
  });

  describe('createArticle', () => {
    it('should navigate to create-post', () => {
      component.createArticle();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/create-post']);
    });
  });

  describe('sortPosts', () => {
    it('should toggle sortDescending', () => {
      mockPostService.all.mockReturnValue(of(mockPosts));
      mockTopicService.getUserSubscriptions.mockReturnValue(of([]));
      component.ngOnInit();

      const initialSort = component.sortDescending;
      component.sortPosts();

      expect(component.sortDescending).toBe(!initialSort);
    });
  });

  describe('toggleShowAllPosts', () => {
    it('should toggle showAllPosts', () => {
      mockPostService.all.mockReturnValue(of(mockPosts));
      mockTopicService.getUserSubscriptions.mockReturnValue(of([]));
      component.ngOnInit();

      const initialValue = component.showAllPosts;
      component.toggleShowAllPosts();

      expect(component.showAllPosts).toBe(!initialValue);
    });
  });

  describe('hasSubscriptions getter', () => {
    it('should return true when user has subscriptions', () => {
      component.subscribedTopics = mockTopics;

      expect(component.hasSubscriptions).toBe(true);
    });

    it('should return false when user has no subscriptions', () => {
      component.subscribedTopics = [];

      expect(component.hasSubscriptions).toBe(false);
    });
  });

  describe('displayedPosts getter', () => {
    it('should return filtered posts', () => {
      mockPostService.all.mockReturnValue(of(mockPosts));
      mockTopicService.getUserSubscriptions.mockReturnValue(of([]));
      component.ngOnInit();

      expect(component.displayedPosts).toEqual(component.filteredPosts);
    });
  });
});
