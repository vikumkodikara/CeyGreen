export interface Reply {
  id: string;
  body: string;
  authorId: string;
  authorName: string;
  createdAt: string;
}

export interface Post {
  id: string;
  title: string;
  body: string;
  authorId: string;
  authorName: string;
  category: string;
  replies: Reply[];
  replyCount: number;
  upvotes: number;
  views: number;
  createdAt: string;
  updatedAt: string;
}

export interface PostRequest {
  title: string;
  body: string;
  tags?: string[];
  cropType?: string;
}

export interface ReplyRequest {
  body: string;
}
