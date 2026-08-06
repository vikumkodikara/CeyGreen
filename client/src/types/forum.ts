export interface Reply {
  id: string;
  content: string;
  authorId: string;
  authorName: string;
  createdAt: string;
}

export interface Post {
  id: string;
  title: string;
  content: string;
  authorId: string;
  authorName: string;
  category: string;
  replies: Reply[];
  replyCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface PostRequest {
  title: string;
  content: string;
  authorId: string;
  authorName?: string;
  category?: string;
}

export interface ReplyRequest {
  content: string;
  authorId: string;
  authorName?: string;
}
