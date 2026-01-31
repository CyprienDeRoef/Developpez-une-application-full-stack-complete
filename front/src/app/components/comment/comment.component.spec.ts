import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommentComponent } from './comment.component';
import { Comment } from '../../interfaces/comment.interface';

describe('CommentComponent', () => {
  let component: CommentComponent;
  let fixture: ComponentFixture<CommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should accept comment input', () => {
    const mockComment: Comment = {
      id: 1,
      content: 'Test comment',
      authorName: 'Test Author',
      authorId: 2,
      createdAt: new Date(),
      postId: 1,
    };

    component.comment = mockComment;
    fixture.detectChanges();

    expect(component.comment).toEqual(mockComment);
  });
});
