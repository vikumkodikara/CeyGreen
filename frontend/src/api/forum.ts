import { apiClient } from './client';
import { Post, PostRequest, ReplyRequest } from '../types/forum';

export const listPosts = async (category?: string, sort?: string): Promise<Post[]> => {
  const params: Record<string, string> = {};
  if (category) params.category = category;
  if (sort) params.sort = sort;
  const res = await apiClient.get<any>('/forum/posts', { params });
  return res.data.content || res.data;
};

export const getPost = async (id: string): Promise<Post> => {
  const res = await apiClient.get<Post>(`/forum/posts/${id}`);
  return res.data;
};

export const createPost = async (data: PostRequest): Promise<Post> => {
  const res = await apiClient.post<Post>('/forum/posts', data);
  return res.data;
};

export const addReply = async (postId: string, data: ReplyRequest): Promise<Post> => {
  const res = await apiClient.post<Post>(`/forum/posts/${postId}/replies`, data);
  return res.data;
};

export const deletePost = async (id: string): Promise<void> => {
  await apiClient.delete(`/forum/posts/${id}`);
};
