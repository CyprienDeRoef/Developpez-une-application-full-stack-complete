export interface Comment {
  id: number;
  content: string;
  authorId: number;
  authorName: string;
  postId: number;
  createdAt: Date;
}
