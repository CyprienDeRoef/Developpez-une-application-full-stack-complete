import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Topic } from 'src/app/interfaces/topic.interface';

@Injectable({
  providedIn: 'root',
})
export class TopicService {
  private pathService = 'api/topics';

  constructor(private httpClient: HttpClient) {}

  /**
   * Get all topics
   * @returns Observable with array of topics
   */
  public all(): Observable<Topic[]> {
    return this.httpClient.get<Topic[]>(this.pathService);
  }

  /**
   * Get a topic by ID
   * @param id - Topic ID
   * @returns Observable with topic details
   */
  public detail(id: number): Observable<Topic> {
    return this.httpClient.get<Topic>(`${this.pathService}/${id}`);
  }

  /**
   * Subscribe to a topic
   * @param topicId - Topic ID to subscribe to
   * @returns Observable<void>
   */
  public subscribe(topicId: number): Observable<void> {
    return this.httpClient.post<void>(
      `${this.pathService}/${topicId}/subscribe`,
      {},
    );
  }

  /**
   * Unsubscribe from a topic
   * @param topicId - Topic ID to unsubscribe from
   * @returns Observable<void>
   */
  public unsubscribe(topicId: number): Observable<void> {
    return this.httpClient.delete<void>(
      `${this.pathService}/${topicId}/subscribe`,
    );
  }

  /**
   * Get user's subscribed topics
   * @returns Observable with array of subscribed topics
   */
  public getUserSubscriptions(): Observable<Topic[]> {
    return this.httpClient.get<Topic[]>(`${this.pathService}/subscriptions`);
  }
}
