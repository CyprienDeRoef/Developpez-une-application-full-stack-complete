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

  public all(): Observable<Post[]> {
    return this.httpClient.get<Post[]>(this.pathService);
  }

  public detail(id: string): Observable<Post> {
    return this.httpClient.get<Post>(`${this.pathService}/${id}`);
  }

  public create(request: CreatePostRequest): Observable<Post> {
    return this.httpClient.post<Post>(this.pathService, request);
  }

  public update(id: string, request: CreatePostRequest): Observable<Post> {
    return this.httpClient.put<Post>(`${this.pathService}/${id}`, request);
  }

  public delete(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.pathService}/${id}`);
  }

  public getByAuthor(authorId: string): Observable<Post[]> {
    return this.httpClient.get<Post[]>(
      `${this.pathService}/author/${authorId}`,
    );
  }

  public getByTopic(topicId: string): Observable<Post[]> {
    return this.httpClient.get<Post[]>(`${this.pathService}/topic/${topicId}`);
  }
}
