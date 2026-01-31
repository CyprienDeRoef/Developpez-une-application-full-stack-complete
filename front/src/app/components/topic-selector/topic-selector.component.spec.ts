import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TopicSelectorComponent } from './topic-selector.component';
import { Topic } from '../../interfaces/topic.interface';

describe('TopicSelectorComponent', () => {
  let component: TopicSelectorComponent;
  let fixture: ComponentFixture<TopicSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TopicSelectorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TopicSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onTopicChange', () => {
    it('should emit topicIdChange event with value', () => {
      const emitSpy = jest.spyOn(component.topicIdChange, 'emit');

      component.onTopicChange('123');

      expect(emitSpy).toHaveBeenCalledWith('123');
    });
  });

  describe('onAddTopic', () => {
    it('should emit topicAdded event with parsed topicId', () => {
      component.topicId = '123';
      const emitSpy = jest.spyOn(component.topicAdded, 'emit');

      component.onAddTopic();

      expect(emitSpy).toHaveBeenCalledWith(123);
    });

    it('should not emit topicAdded event when topicId is null', () => {
      component.topicId = null;
      const emitSpy = jest.spyOn(component.topicAdded, 'emit');

      component.onAddTopic();

      expect(emitSpy).not.toHaveBeenCalled();
    });
  });

  describe('onRemoveTopic', () => {
    it('should emit topicRemoved event with topic', () => {
      const mockTopic: Topic = {
        id: 1,
        name: 'Test Topic',
        description: 'Test description',
      };
      const emitSpy = jest.spyOn(component.topicRemoved, 'emit');

      component.onRemoveTopic(mockTopic);

      expect(emitSpy).toHaveBeenCalledWith(mockTopic);
    });
  });

  describe('isTopicDisabled', () => {
    it('should return true when topic is in selectedTopics', () => {
      const mockTopic: Topic = {
        id: 1,
        name: 'Test Topic',
        description: 'Test description',
      };
      component.selectedTopics = [mockTopic];

      expect(component.isTopicDisabled(1)).toBe(true);
    });

    it('should return false when topic is not in selectedTopics', () => {
      const mockTopic: Topic = {
        id: 1,
        name: 'Test Topic',
        description: 'Test description',
      };
      component.selectedTopics = [mockTopic];

      expect(component.isTopicDisabled(2)).toBe(false);
    });

    it('should return false when selectedTopics is empty', () => {
      component.selectedTopics = [];

      expect(component.isTopicDisabled(1)).toBe(false);
    });
  });

  describe('Input properties', () => {
    it('should have default values', () => {
      expect(component.topics).toEqual([]);
      expect(component.selectedTopics).toEqual([]);
      expect(component.topicId).toBeNull();
    });
  });
});
