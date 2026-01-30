import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Post } from '../../interfaces/post.interface';
import { Topic } from '../../interfaces/topic.interface';
import { PostService } from '../../services/post.service';
import { TopicService } from '../../services/topic.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent implements OnInit {
  allPosts: Post[] = [];
  filteredPosts: Post[] = [];
  subscribedTopics: Topic[] = [];
  isLoading: boolean = true;
  sortDescending: boolean = true; // true = newest first, false = oldest first
  showAllPosts: boolean = false;

  constructor(
    private router: Router,
    private postService: PostService,
    private topicService: TopicService,
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;

    forkJoin({
      posts: this.postService.all(),
      subscriptions: this.topicService.getUserSubscriptions(),
    }).subscribe({
      next: ({ posts, subscriptions }) => {
        this.allPosts = posts;
        this.subscribedTopics = subscriptions;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading data:', error);
        this.isLoading = false;
      },
    });
  }

  createArticle(): void {
    this.router.navigate(['/create-post']);
  }

  sortPosts(): void {
    this.sortDescending = !this.sortDescending;
    this.applyFilters();
  }

  toggleShowAllPosts(): void {
    this.showAllPosts = !this.showAllPosts;
    this.applyFilters();
  }

  private applyFilters(): void {
    let posts = [...this.allPosts];

    // Filter by subscribed topics if not showing all posts and user has subscriptions
    if (!this.showAllPosts && this.subscribedTopics.length > 0) {
      const subscribedTopicNames = this.subscribedTopics.map((t) => t.name);
      posts = posts.filter((post) =>
        post.topicNames.some((topicName) =>
          subscribedTopicNames.includes(topicName),
        ),
      );
    }

    // Apply sorting
    posts.sort((a, b) => {
      const dateA = new Date(a.createdAt).getTime();
      const dateB = new Date(b.createdAt).getTime();
      return this.sortDescending ? dateB - dateA : dateA - dateB;
    });

    this.filteredPosts = posts;
  }

  get hasSubscriptions(): boolean {
    return this.subscribedTopics.length > 0;
  }

  get displayedPosts(): Post[] {
    return this.filteredPosts;
  }
}
