import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { PostDetailComponent } from './post-detail.component';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { Post } from '../../interfaces/post.interface';
import { Comment } from '../../interfaces/comment.interface';

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let mockPostService: jest.Mocked<PostService>;
  let mockCommentService: jest.Mocked<CommentService>;
  let mockRouter: jest.Mocked<Router>;
  let mockActivatedRoute: any;

  const mockPost: Post = {
    id: 1,
    title: 'Test Post',
    content: 'Test Content',
    authorName: 'Test Author',
    authorId: 1,
    topicNames: ['Topic 1'],
    topicIds: [1],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockComments: Comment[] = [
    {
      id: 1,
      content: 'Test Comment',
      authorName: 'Comment Author',
      authorId: 2,
      createdAt: new Date(),
      postId: 1,
    },
  ];

  beforeEach(async () => {
    mockPostService = {
      detail: jest.fn(),
    } as any;

    mockCommentService = {
      getCommentsByPostId: jest.fn(),
      createComment: jest.fn(),
    } as any;

    mockRouter = {
      navigate: jest.fn(),
    } as any;

    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1'),
        },
      },
    };

    await TestBed.configureTestingModule({
      declarations: [PostDetailComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: PostService, useValue: mockPostService },
        { provide: CommentService, useValue: mockCommentService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize comment form', () => {
      mockPostService.detail.mockReturnValue(of(mockPost));
      mockCommentService.getCommentsByPostId.mockReturnValue(of(mockComments));

      component.ngOnInit();

      expect(component.commentForm).toBeDefined();
      expect(component.commentForm.get('content')).toBeDefined();
    });

    it('should load post and comments', () => {
      mockPostService.detail.mockReturnValue(of(mockPost));
      mockCommentService.getCommentsByPostId.mockReturnValue(of(mockComments));

      component.ngOnInit();

      expect(mockPostService.detail).toHaveBeenCalledWith('1');
      expect(mockCommentService.getCommentsByPostId).toHaveBeenCalledWith('1');
    });
  });

  describe('loadPost', () => {
    it('should set post and loading state', () => {
      mockPostService.detail.mockReturnValue(of(mockPost));

      component.loadPost('1');

      expect(component.post).toEqual(mockPost);
      expect(component.isLoading).toBe(false);
    });

    it('should handle error', () => {
      mockPostService.detail.mockReturnValue(
        throwError(() => new Error('Error')),
      );

      component.loadPost('1');

      expect(component.errorMessage).toBe("Impossible de charger l'article");
      expect(component.isLoading).toBe(false);
    });
  });

  describe('loadComments', () => {
    it('should set comments', () => {
      mockCommentService.getCommentsByPostId.mockReturnValue(of(mockComments));

      component.loadComments('1');

      expect(component.comments).toEqual(mockComments);
    });
  });

  describe('goBack', () => {
    it('should navigate to feed', () => {
      component.goBack();

      expect(mockRouter.navigate).toHaveBeenCalledWith(['/feed']);
    });
  });

  describe('onSubmitComment', () => {
    beforeEach(() => {
      mockPostService.detail.mockReturnValue(of(mockPost));
      mockCommentService.getCommentsByPostId.mockReturnValue(of(mockComments));
      component.ngOnInit();
      component.commentForm.patchValue({ content: 'New comment' });
    });

    it('should create comment when form is valid', () => {
      const newComment: Comment = {
        id: 2,
        content: 'New comment',
        authorName: 'Test Author',
        authorId: 1,
        createdAt: new Date(),
        postId: 1,
      };
      mockCommentService.createComment.mockReturnValue(of(newComment));

      component.onSubmitComment();

      expect(mockCommentService.createComment).toHaveBeenCalledWith('1', {
        content: 'New comment',
      });
    });

    it('should add comment to list after creation', () => {
      const newComment: Comment = {
        id: 2,
        content: 'New comment',
        authorName: 'Test Author',
        authorId: 1,
        createdAt: new Date(),
        postId: 1,
      };
      mockCommentService.createComment.mockReturnValue(of(newComment));
      component.comments = [...mockComments];

      component.onSubmitComment();

      expect(component.comments.length).toBe(3);
    });

    it('should reset form after successful submission', () => {
      const newComment: Comment = {
        id: 2,
        content: 'New comment',
        authorName: 'Test Author',
        authorId: 1,
        createdAt: new Date(),
        postId: 1,
      };
      mockCommentService.createComment.mockReturnValue(of(newComment));

      component.onSubmitComment();

      expect(component.commentForm.value.content).toBeNull();
    });

    it('should not submit when form is invalid', () => {
      component.commentForm.patchValue({ content: '' });

      component.onSubmitComment();

      expect(mockCommentService.createComment).not.toHaveBeenCalled();
    });
  });
});
