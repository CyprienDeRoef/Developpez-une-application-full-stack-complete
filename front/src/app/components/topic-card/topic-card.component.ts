import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Topic } from '../../interfaces/topic.interface';

@Component({
  selector: 'app-topic-card',
  templateUrl: './topic-card.component.html',
  styleUrls: ['./topic-card.component.scss'],
})
export class TopicCardComponent {
  @Input() topic!: Topic;
  @Input() isSubscribed: boolean = false;
  @Input() showDescription: boolean = true;
  @Input() mode: 'subscribe' | 'unsubscribe' = 'subscribe';
  @Output() actionClicked = new EventEmitter<Topic>();

  onActionClick(): void {
    this.actionClicked.emit(this.topic);
  }

  get buttonText(): string {
    if (this.mode === 'subscribe') {
      return this.isSubscribed ? 'Déjà abonné' : "S'abonner";
    }
    return 'Se désabonner';
  }

  get buttonClass(): string {
    if (this.mode === 'subscribe') {
      return this.isSubscribed
        ? 'btn-secondary'
        : 'btn-primary btn-primary-custom';
    }
    return 'btn-primary btn-primary-custom';
  }
}
