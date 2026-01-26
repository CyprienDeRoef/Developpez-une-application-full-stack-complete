import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Post } from '../../interfaces/post.interface';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  isLoading: boolean = true;

  constructor(
    private router: Router,
    private postService: PostService,
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.isLoading = true;
    this.postService.all().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading posts:', error);
        this.isLoading = false;
      },
    });
  }

  createArticle(): void {
    this.router.navigate(['/create-post']);
  }

  sortPosts(): void {
    // TODO: Implement sorting
  }
}
