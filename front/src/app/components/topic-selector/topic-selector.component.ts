import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Topic } from '../../interfaces/topic.interface';

@Component({
  selector: 'app-topic-selector',
  templateUrl: './topic-selector.component.html',
  styleUrls: ['./topic-selector.component.scss'],
})
export class TopicSelectorComponent {
  @Input() topics: Topic[] = [];
  @Input() selectedTopics: Topic[] = [];
  @Input() topicId: string | null = null;
  @Output() topicAdded = new EventEmitter<number>();
  @Output() topicRemoved = new EventEmitter<Topic>();
  @Output() topicIdChange = new EventEmitter<string>();

  onTopicChange(value: string): void {
    this.topicIdChange.emit(value);
  }

  onAddTopic(): void {
    if (this.topicId) {
      this.topicAdded.emit(parseInt(this.topicId));
    }
  }

  onRemoveTopic(topic: Topic): void {
    this.topicRemoved.emit(topic);
  }

  isTopicDisabled(topicId: number): boolean {
    return this.selectedTopics.some((t) => t.id === topicId);
  }
}
