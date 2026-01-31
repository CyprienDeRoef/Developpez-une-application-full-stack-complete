import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TopicCardComponent } from './topic-card.component';
import { Topic } from '../../interfaces/topic.interface';

describe('TopicCardComponent', () => {
  let component: TopicCardComponent;
  let fixture: ComponentFixture<TopicCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TopicCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TopicCardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onActionClick', () => {
    it('should emit actionClicked event with topic', () => {
      const mockTopic: Topic = {
        id: 1,
        name: 'Test Topic',
        description: 'Test description',
      };
      component.topic = mockTopic;
      const emitSpy = jest.spyOn(component.actionClicked, 'emit');

      component.onActionClick();

      expect(emitSpy).toHaveBeenCalledWith(mockTopic);
    });
  });

  describe('buttonText getter', () => {
    it('should return "S\'abonner" when mode is subscribe and not subscribed', () => {
      component.mode = 'subscribe';
      component.isSubscribed = false;

      expect(component.buttonText).toBe("S'abonner");
    });

    it('should return "Déjà abonné" when mode is subscribe and subscribed', () => {
      component.mode = 'subscribe';
      component.isSubscribed = true;

      expect(component.buttonText).toBe('Déjà abonné');
    });

    it('should return "Se désabonner" when mode is unsubscribe', () => {
      component.mode = 'unsubscribe';

      expect(component.buttonText).toBe('Se désabonner');
    });
  });

  describe('buttonClass getter', () => {
    it('should return "btn-primary btn-primary-custom" when mode is subscribe and not subscribed', () => {
      component.mode = 'subscribe';
      component.isSubscribed = false;

      expect(component.buttonClass).toBe('btn-primary btn-primary-custom');
    });

    it('should return "btn-secondary" when mode is subscribe and subscribed', () => {
      component.mode = 'subscribe';
      component.isSubscribed = true;

      expect(component.buttonClass).toBe('btn-secondary');
    });

    it('should return "btn-primary btn-primary-custom" when mode is unsubscribe', () => {
      component.mode = 'unsubscribe';

      expect(component.buttonClass).toBe('btn-primary btn-primary-custom');
    });
  });

  describe('Input properties', () => {
    it('should have default values', () => {
      expect(component.isSubscribed).toBe(false);
      expect(component.showDescription).toBe(true);
      expect(component.mode).toBe('subscribe');
    });

    it('should accept topic input', () => {
      const mockTopic: Topic = {
        id: 1,
        name: 'Test Topic',
        description: 'Test description',
      };

      component.topic = mockTopic;
      fixture.detectChanges();

      expect(component.topic).toEqual(mockTopic);
    });
  });
});
