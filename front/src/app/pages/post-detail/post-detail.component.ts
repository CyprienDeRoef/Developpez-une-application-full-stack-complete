import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { Post } from '../../interfaces/post.interface';
import { Comment } from '../../interfaces/comment.interface';

@Component({
  selector: 'app-post-detail',
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.scss'],
})
export class PostDetailComponent implements OnInit {
  post?: Post;
  comments: Comment[] = [];
  commentForm!: FormGroup;
  isLoading: boolean = true;
  isSubmitting: boolean = false;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private postService: PostService,
    private commentService: CommentService,
  ) {}

  ngOnInit(): void {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(1)]],
    });

    const postId = this.route.snapshot.paramMap.get('id');
    if (postId) {
      this.loadPost(postId);
      this.loadComments(postId);
    }
  }

  loadPost(postId: string): void {
    this.postService.detail(postId).subscribe({
      next: (post) => {
        this.post = post;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading post:', error);
        this.isLoading = false;
        this.errorMessage = "Impossible de charger l'article";
      },
    });
  }

  loadComments(postId: string): void {
    this.commentService.getCommentsByPostId(postId).subscribe({
      next: (comments) => {
        this.comments = comments;
      },
      error: (error) => {
        console.error('Error loading comments:', error);
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/feed']);
  }

  onSubmitComment(): void {
    if (this.commentForm.valid && this.post) {
      this.isSubmitting = true;
      this.errorMessage = '';

      const request = {
        content: this.commentForm.value.content,
      };

      this.commentService
        .createComment(this.post.id.toString(), request)
        .subscribe({
          next: (comment) => {
            this.comments.push(comment);
            this.commentForm.reset();
            this.isSubmitting = false;
          },
          error: (error) => {
            console.error('Error creating comment:', error);
            this.isSubmitting = false;
            this.errorMessage = 'Impossible de publier le commentaire';
          },
        });
    }
  }
}
