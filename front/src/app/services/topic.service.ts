import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Topic {
  id: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root',
})
export class TopicService {
  private pathService = 'api/topics';

  constructor(private httpClient: HttpClient) {}

  public all(): Observable<Topic[]> {
    return this.httpClient.get<Topic[]>(this.pathService);
  }

  public detail(id: string): Observable<Topic> {
    return this.httpClient.get<Topic>(`${this.pathService}/${id}`);
  }

  public subscribe(topicId: string): Observable<void> {
    return this.httpClient.post<void>(
      `${this.pathService}/${topicId}/subscribe`,
      {},
    );
  }

  public unsubscribe(topicId: string): Observable<void> {
    return this.httpClient.delete<void>(
      `${this.pathService}/${topicId}/subscribe`,
    );
  }

  public getUserSubscriptions(): Observable<Topic[]> {
    return this.httpClient.get<Topic[]>(`${this.pathService}/subscriptions`);
  }
}
