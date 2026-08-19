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
  tags?: string[];
  replies: Reply[];
  replyCount: number;
  upvotes: number;
  downvotes: number;
  upvotedBy: string[];
  downvotedBy: string[];
  views: number;
  flagCount?: number;
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
