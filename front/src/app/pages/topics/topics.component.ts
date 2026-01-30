import { Component, OnInit } from '@angular/core';
import { TopicService } from '../../services/topic.service';
import { Topic } from 'src/app/interfaces/topic.interface';

@Component({
  selector: 'app-topics',
  templateUrl: './topics.component.html',
  styleUrls: ['./topics.component.scss'],
})
export class TopicsComponent implements OnInit {
  topics: Topic[] = [];
  subscribedTopicIds: Set<number> = new Set();
  isLoading: boolean = true;

  constructor(private topicService: TopicService) {}

  ngOnInit(): void {
    this.loadTopics();
    this.loadSubscriptions();
  }

  loadTopics(): void {
    this.topicService.all().subscribe({
      next: (topics) => {
        this.topics = topics;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading topics:', error);
        this.isLoading = false;
      },
    });
  }

  loadSubscriptions(): void {
    this.topicService.getUserSubscriptions().subscribe({
      next: (subscribedTopics) => {
        this.subscribedTopicIds = new Set(
          subscribedTopics.map((topic) => topic.id),
        );
      },
      error: (error) => {
        console.error('Error loading subscriptions:', error);
      },
    });
  }

  isSubscribed(topicId: number): boolean {
    return this.subscribedTopicIds.has(topicId);
  }

  toggleSubscription(topic: Topic): void {
    if (this.isSubscribed(topic.id)) {
      this.unsubscribe(topic);
    } else {
      this.subscribe(topic);
    }
  }

  subscribe(topic: Topic): void {
    this.topicService.subscribe(topic.id).subscribe({
      next: () => {
        this.subscribedTopicIds.add(topic.id);
      },
      error: (error) => {
        console.error('Error subscribing:', error);
      },
    });
  }

  unsubscribe(topic: Topic): void {
    this.topicService.unsubscribe(topic.id).subscribe({
      next: () => {
        this.subscribedTopicIds.delete(topic.id);
      },
      error: (error) => {
        console.error('Error unsubscribing:', error);
      },
    });
  }
}
