import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { TopicsComponent } from './topics.component';
import { TopicService } from '../../services/topic.service';
import { Topic } from '../../interfaces/topic.interface';

describe('TopicsComponent', () => {
  let component: TopicsComponent;
  let fixture: ComponentFixture<TopicsComponent>;
  let mockTopicService: jest.Mocked<TopicService>;

  const mockTopics: Topic[] = [
    { id: 1, name: 'Topic 1', description: 'Description 1' },
    { id: 2, name: 'Topic 2', description: 'Description 2' },
  ];

  beforeEach(async () => {
    mockTopicService = {
      all: jest.fn(),
      getUserSubscriptions: jest.fn(),
      subscribe: jest.fn(),
      unsubscribe: jest.fn(),
    } as any;

    await TestBed.configureTestingModule({
      declarations: [TopicsComponent],
      providers: [{ provide: TopicService, useValue: mockTopicService }],
    }).compileComponents();

    fixture = TestBed.createComponent(TopicsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load topics and subscriptions', () => {
      mockTopicService.all.mockReturnValue(of(mockTopics));
      mockTopicService.getUserSubscriptions.mockReturnValue(
        of([mockTopics[0]]),
      );

      component.ngOnInit();

      expect(mockTopicService.all).toHaveBeenCalled();
      expect(mockTopicService.getUserSubscriptions).toHaveBeenCalled();
    });
  });

  describe('loadTopics', () => {
    it('should set topics and loading state', () => {
      mockTopicService.all.mockReturnValue(of(mockTopics));

      component.loadTopics();

      expect(component.topics).toEqual(mockTopics);
      expect(component.isLoading).toBe(false);
    });

    it('should handle error', () => {
      mockTopicService.all.mockReturnValue(
        throwError(() => new Error('Error')),
      );

      component.loadTopics();

      expect(component.isLoading).toBe(false);
    });
  });

  describe('loadSubscriptions', () => {
    it('should set subscribedTopicIds', () => {
      mockTopicService.getUserSubscriptions.mockReturnValue(
        of([mockTopics[0]]),
      );

      component.loadSubscriptions();

      expect(component.subscribedTopicIds.has(1)).toBe(true);
    });
  });

  describe('isSubscribed', () => {
    it('should return true for subscribed topic', () => {
      component.subscribedTopicIds.add(1);

      expect(component.isSubscribed(1)).toBe(true);
    });

    it('should return false for non-subscribed topic', () => {
      expect(component.isSubscribed(1)).toBe(false);
    });
  });

  describe('toggleSubscription', () => {
    it('should call subscribe when not subscribed', () => {
      mockTopicService.subscribe.mockReturnValue(of(undefined));
      component.subscribedTopicIds = new Set();

      component.toggleSubscription(mockTopics[0]);

      expect(mockTopicService.subscribe).toHaveBeenCalledWith(1);
    });

    it('should call unsubscribe when subscribed', () => {
      mockTopicService.unsubscribe.mockReturnValue(of(undefined));
      component.subscribedTopicIds = new Set([1]);

      component.toggleSubscription(mockTopics[0]);

      expect(mockTopicService.unsubscribe).toHaveBeenCalledWith(1);
    });
  });

  describe('subscribe', () => {
    it('should add topic to subscribedTopicIds', () => {
      mockTopicService.subscribe.mockReturnValue(of(undefined));
      component.subscribedTopicIds = new Set();

      component.subscribe(mockTopics[0]);

      expect(component.subscribedTopicIds.has(1)).toBe(true);
    });
  });

  describe('unsubscribe', () => {
    it('should remove topic from subscribedTopicIds', () => {
      mockTopicService.unsubscribe.mockReturnValue(of(undefined));
      component.subscribedTopicIds = new Set([1]);

      component.unsubscribe(mockTopics[0]);

      expect(component.subscribedTopicIds.has(1)).toBe(false);
    });
  });
});
