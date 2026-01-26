import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../interfaces/post.interface';

export interface CreatePostRequest {
  title: string;
  content: string;
  topicIds: number[];
}

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private pathService = 'api/posts';

  constructor(private httpClient: HttpClient) {}

  /**
   * Get all posts
   * @returns Observable with array of posts
   */
  public all(): Observable<Post[]> {
    return this.httpClient.get<Post[]>(this.pathService);
  }

  /**
   * Get a post by ID
   * @param id - Post ID
   * @returns Observable with post details
   */
  public detail(id: string): Observable<Post> {
    return this.httpClient.get<Post>(`${this.pathService}/${id}`);
  }

  /**
   * Create a new post
   * @param request - Post creation request with title, content, and topic IDs
   * @returns Observable with created post
   */
  public create(request: CreatePostRequest): Observable<Post> {
    return this.httpClient.post<Post>(this.pathService, request);
  }

  /**
   * Update an existing post
   * @param id - Post ID to update
   * @param request - Post update request with title, content, and topic IDs
   * @returns Observable with updated post
   */
  public update(id: string, request: CreatePostRequest): Observable<Post> {
    return this.httpClient.put<Post>(`${this.pathService}/${id}`, request);
  }

  /**
   * Delete a post
   * @param id - Post ID to delete
   * @returns Observable<void>
   */
  public delete(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.pathService}/${id}`);
  }

  /**
   * Get all posts by a specific author
   * @param authorId - Author ID
   * @returns Observable with array of posts
   */
  public getByAuthor(authorId: string): Observable<Post[]> {
    return this.httpClient.get<Post[]>(
      `${this.pathService}/author/${authorId}`,
    );
  }

  /**
   * Get all posts for a specific topic
   * @param topicId - Topic ID
   * @returns Observable with array of posts
   */
  public getByTopic(topicId: string): Observable<Post[]> {
    return this.httpClient.get<Post[]>(`${this.pathService}/topic/${topicId}`);
  }
}
