import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { TopicService } from '../../services/topic.service';
import { Topic } from 'src/app/interfaces/topic.interface';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss'],
})
export class CreatePostComponent implements OnInit {
  postForm!: FormGroup;
  topics: Topic[] = [];
  selectedTopics: Topic[] = [];
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
      topicId: [''],
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

  addTopic(): void {
    const topicId = this.postForm.value.topicId;
    if (topicId) {
      const topic = this.topics.find((t) => t.id === parseInt(topicId));
      if (topic && !this.selectedTopics.find((t) => t.id === topic.id)) {
        this.selectedTopics.push(topic);
        this.postForm.patchValue({ topicId: '' });
      }
    }
  }

  onTopicIdChange(topicId: string): void {
    this.postForm.patchValue({ topicId });
  }

  onTopicAdded(topicId: number): void {
    const topic = this.topics.find((t) => t.id === topicId);
    if (topic && !this.selectedTopics.find((t) => t.id === topic.id)) {
      this.selectedTopics.push(topic);
      this.postForm.patchValue({ topicId: '' });
    }
  }

  onTopicRemoved(topic: Topic): void {
    this.removeTopic(topic);
  }

  removeTopic(topic: Topic): void {
    this.selectedTopics = this.selectedTopics.filter((t) => t.id !== topic.id);
  }

  goBack(): void {
    this.router.navigate(['/feed']);
  }

  onSubmit(): void {
    if (this.postForm.valid && this.selectedTopics.length > 0) {
      this.isLoading = true;
      this.errorMessage = '';

      const request = {
        title: this.postForm.value.title,
        content: this.postForm.value.content,
        topicIds: this.selectedTopics.map((t) => t.id),
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
    } else if (this.selectedTopics.length === 0) {
      this.errorMessage = 'Veuillez sélectionner au moins un thème';
    }
  }
}
