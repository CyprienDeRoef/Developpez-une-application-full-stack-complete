import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment } from '../interfaces/comment.interface';

export interface CreateCommentRequest {
  content: string;
}

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private pathService = 'api/posts';

  constructor(private httpClient: HttpClient) {}

  /**
   * Get all comments for a specific post
   * @param postId - Post ID
   * @returns Observable with array of comments
   */
  public getCommentsByPostId(postId: string): Observable<Comment[]> {
    return this.httpClient.get<Comment[]>(
      `${this.pathService}/${postId}/comments`,
    );
  }

  /**
   * Create a new comment on a post
   * @param postId - Post ID to comment on
   * @param request - Comment creation request with content
   * @returns Observable with created comment
   */
  public createComment(
    postId: string,
    request: CreateCommentRequest,
  ): Observable<Comment> {
    return this.httpClient.post<Comment>(
      `${this.pathService}/${postId}/comments`,
      request,
    );
  }
}
