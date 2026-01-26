import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { TopicService, Topic } from '../../services/topic.service';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss'],
})
export class CreatePostComponent implements OnInit {
  postForm!: FormGroup;
  topics: Topic[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private postService: PostService,
    private topicService: TopicService,
  ) {}

  ngOnInit(): void {
    this.postForm = this.fb.group({
      topicId: ['', Validators.required],
      title: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(10)]],
    });

    this.loadTopics();
  }

  loadTopics(): void {
    this.topicService.all().subscribe({
      next: (topics) => {
        this.topics = topics;
      },
      error: (error) => {
        console.error('Error loading topics:', error);
        this.errorMessage = 'Impossible de charger les thèmes';
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/feed']);
  }

  onSubmit(): void {
    if (this.postForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const request = {
        title: this.postForm.value.title,
        content: this.postForm.value.content,
        topicIds: [parseInt(this.postForm.value.topicId)],
      };

      this.postService.create(request).subscribe({
        next: (post) => {
          console.log('Post created:', post);
          this.isLoading = false;
          this.router.navigate(['/feed']);
        },
        error: (error) => {
          console.error('Error creating post:', error);
          this.isLoading = false;
          this.errorMessage =
            "Une erreur est survenue lors de la création de l'article";
        },
      });
    }
  }
}
