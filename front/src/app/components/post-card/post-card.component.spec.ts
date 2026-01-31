import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostCardComponent } from './post-card.component';
import { Post } from '../../interfaces/post.interface';

describe('PostCardComponent', () => {
  let component: PostCardComponent;
  let fixture: ComponentFixture<PostCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PostCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PostCardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should accept post input', () => {
    const mockPost: Post = {
      id: 1,
      title: 'Test Post',
      content: 'Test content',
      authorName: 'Test Author',
      authorId: 1,
      topicNames: ['Topic 1'],
      topicIds: [1],
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    component.post = mockPost;
    fixture.detectChanges();

    expect(component.post).toEqual(mockPost);
  });
});
