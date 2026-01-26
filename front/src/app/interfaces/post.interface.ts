export interface Post {
  id: number;
  title: string;
  content: string;
  authorId: number;
  authorName: string;
  topicIds: number[];
  topicNames: string[];
  createdAt: Date;
  updatedAt: Date;
}
