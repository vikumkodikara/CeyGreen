import React, { useState, useEffect } from 'react';
import { listPosts, createPost, addReply, getPost } from '../api/forum';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Post } from '../types/forum';
import { useAuth } from '../hooks/useAuth';
import './ForumPage.css';

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
    <div className="forum-layout-container">
      <h1 className="forum-header-title">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
        Community Forum
      </h1>
      <p className="forum-header-subtitle">{posts.length} Posts</p>

      <div className="forum-grid">
        <div className="post-feed-column">
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

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        {posts.map((post) => (
          <div key={post.id} className="forum-post-card">
            <div className="post-header">
              <div className="post-avatar">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#000" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
              </div>
              <div className="post-meta-info">
                <span className="post-author">Posted by {post.authorName || post.authorId}</span>
                <span className="post-time">{new Date(post.createdAt || Date.now()).toLocaleDateString()}</span>
              </div>
            </div>

            <div className="post-title">{post.title}</div>
            <div className="post-body">
              {post.body}
              {post.body && post.body.length > 100 && <span className="post-see-more">see more</span>}
            </div>

            <div className="post-actions">
              <div className="action-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="19" x2="12" y2="5"></line><polyline points="5 12 12 5 19 12"></polyline></svg>
                {post.upvotes || '5k'}
              </div>
              <div className="action-item action-item-muted">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg>
                40
              </div>
              <div className="action-item action-item-muted" style={{ marginLeft: '0.5rem' }}>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="1"></circle><circle cx="12" cy="5" r="1"></circle><circle cx="12" cy="19" r="1"></circle></svg>
              </div>
              <div className="action-spacer"></div>
              <div className="action-item action-item-muted">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                2K
              </div>
              <div className="action-item action-item-muted">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                {post.replyCount || 0}
              </div>
            </div>

            {/* Replies */}
            {post.replies && post.replies.length > 0 && (
              <div className="post-reply-section">
                {post.replies.map((r, i) => (
                  <div key={i} className="reply-item">
                    <div className="reply-author">{r.authorName || r.authorId}</div>
                    <div className="reply-body">{r.body}</div>
                  </div>
                ))}
              </div>
            )}
            
            {(!post.replies || post.replies.length === 0) && post.replyCount !== undefined && post.replyCount > 0 && (
              <Button size="sm" onClick={() => handleLoadReplies(post.id)} style={{ marginTop: '1rem', background: 'rgba(0,0,0,0.3)', border: 'none' }}>
                View {post.replyCount} {post.replyCount === 1 ? 'reply' : 'replies'}
              </Button>
            )}

            <div className="reply-input-wrapper">
              <input
                placeholder="Write a reply..."
                value={replyText[post.id] || ''}
                onChange={(e) => setReplyText({ ...replyText, [post.id]: e.target.value })}
                onKeyDown={(e) => { if (e.key === 'Enter') handleAddReply(post.id); }}
              />
              <button className="reply-btn" onClick={() => handleAddReply(post.id)}>Reply</button>
            </div>
          </div>
        ))}
          </div>
        </div>

        <div className="sidebar-column">
          <Card>
            <h3 style={{ color: 'var(--text-main)', marginBottom: '1rem' }}>AI Green Assistant</h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Sidebar placeholder</p>
          </Card>
        </div>
      </div>
    </div>
  );
};
