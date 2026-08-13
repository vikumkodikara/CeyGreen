import React, { useState, useEffect } from 'react';
import { listPosts, createPost, addReply, getPost } from '../api/forum';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Post } from '../types/forum';
import { useAuth } from '../hooks/useAuth';

export const ForumPage: React.FC = () => {
  const { user } = useAuth();
  const [posts, setPosts] = useState<Post[]>([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [replyText, setReplyText] = useState<Record<string, string>>({});

  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    try {
      const data = await listPosts();
      setPosts(data);
    } catch {
      setPosts([]);
    }
  };

  const handleCreatePost = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createPost({
        title,
        body: content,
      });
      setTitle('');
      setContent('');
      fetchPosts();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to create post');
    }
  };

  const handleAddReply = async (postId: string) => {
    const text = replyText[postId];
    if (!text) return;

    try {
      const updatedPost = await addReply(postId, {
        body: text,
      });
      setReplyText({ ...replyText, [postId]: '' });
      setPosts(posts.map(p => p.id === postId ? updatedPost : p));
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to post reply');
    }
  };

  const handleLoadReplies = async (postId: string) => {
    try {
      const fullPost = await getPost(postId);
      setPosts(posts.map(p => p.id === postId ? fullPost : p));
    } catch (err) {
      console.error('Failed to load replies', err);
    }
  };

  return (
    <div style={{ maxWidth: '900px' }}>
      <h1 style={{ fontSize: '1.8rem', marginBottom: '1.5rem' }}>💬 Community Forum</h1>

      <Card title="Start a Discussion">
        <form onSubmit={handleCreatePost}>
          <Input label="Title" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="e.g. Pest control strategies for tomatoes" required />
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>Discussion Content</label>
            <textarea
              rows={3}
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Write your question or experience..."
              required
            />
          </div>
          <Button type="submit">Post to Forum</Button>
        </form>
      </Card>

      <div style={{ marginTop: '2rem', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        {posts.map((post) => (
          <Card key={post.id} title={post.title} subtitle={`Posted by ${post.authorName || post.authorId}`}>
            <p style={{ marginTop: '0.5rem', marginBottom: '1rem', color: 'var(--text-main)' }}>{post.body}</p>

            {/* Replies */}
            {post.replies && post.replies.length > 0 && (
              <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: '8px', marginBottom: '1rem' }}>
                <h4 style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>Replies:</h4>
                {post.replies.map((r, i) => (
                  <div key={i} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)', padding: '0.4rem 0' }}>
                    <strong style={{ fontSize: '0.85rem', color: 'var(--accent-green)' }}>{r.authorName || r.authorId}: </strong>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{r.body}</span>
                  </div>
                ))}
              </div>
            )}
            
            {(!post.replies || post.replies.length === 0) && post.replyCount !== undefined && post.replyCount > 0 && (
              <Button size="sm" onClick={() => handleLoadReplies(post.id)} style={{ marginBottom: '1rem' }} variant="secondary">
                View {post.replyCount} {post.replyCount === 1 ? 'reply' : 'replies'}
              </Button>
            )}

            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input
                placeholder="Write a reply..."
                value={replyText[post.id] || ''}
                onChange={(e) => setReplyText({ ...replyText, [post.id]: e.target.value })}
              />
              <Button size="sm" onClick={() => handleAddReply(post.id)}>Reply</Button>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};
