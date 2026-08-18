import React, { useState, useEffect, useRef } from 'react';
import { listPosts, createPost, addReply, getPost, actOnPost, deletePost, reportPost } from '../api/forum';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Post } from '../types/forum';
import { useAuth } from '../hooks/useAuth';
import './ForumPage.css';

export const ForumPage: React.FC = () => {
  const { user } = useAuth();
  const [posts, setPosts] = useState<Post[]>([]);
  const [sort, setSort] = useState<'newest' | 'trending'>('trending');
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [tags, setTags] = useState('');
  const [replyText, setReplyText] = useState<Record<string, string>>({});
  const [expandedPosts, setExpandedPosts] = useState<Record<string, boolean>>({});
  const replyInputRefs = useRef<Record<string, HTMLInputElement | null>>({});

  const [activeMenuPostId, setActiveMenuPostId] = useState<string | null>(null);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);
  const [deleteConfirmPostId, setDeleteConfirmPostId] = useState<string | null>(null);

  const showToast = (message: string, type: 'success' | 'error' = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3000);
  };
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setActiveMenuPostId(null);
      }
    };
    if (activeMenuPostId) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [activeMenuPostId]);

  const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const [reportPostId, setReportPostId] = useState<string | null>(null);
  const [reportType, setReportType] = useState('SPAM');

  const handleOpenReportModal = (postId: string) => {
    setReportPostId(postId);
    setIsReportModalOpen(true);
    setActiveMenuPostId(null);
  };

  const handleSubmitReport = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!reportPostId) return;
    try {
      await reportPost(reportPostId, reportType);
      showToast('Post reported for review.', 'success');
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Failed to report post', 'error');
    } finally {
      setIsReportModalOpen(false);
      setReportPostId(null);
    }
  };

  const executeDelete = async () => {
    if (!deleteConfirmPostId) return;
    try {
      await deletePost(deleteConfirmPostId);
      setPosts(prev => prev.filter(p => p.id !== deleteConfirmPostId));
      showToast('Post deleted successfully', 'success');
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Failed to delete post', 'error');
    }
    setDeleteConfirmPostId(null);
    setActiveMenuPostId(null);
  };

  const handleDeletePost = (postId: string) => {
    setDeleteConfirmPostId(postId);
  };

  useEffect(() => {
    fetchPosts();
  }, [sort]);

  const fetchPosts = async () => {
    try {
      const data = await listPosts(undefined, sort);
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
        tags: tags.split(',').map(t => t.trim()).filter(Boolean),
      });
      setTitle('');
      setContent('');
      setTags('');
      fetchPosts();
      showToast('Post created successfully', 'success');
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Failed to create post', 'error');
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
      setPosts(sortPostsArray(posts.map(p => p.id === postId ? updatedPost : p)));
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Failed to post reply', 'error');
    }
  };

  const handleLoadReplies = async (postId: string) => {
    try {
      const fullPost = await getPost(postId);
      setPosts(sortPostsArray(posts.map(p => p.id === postId ? fullPost : p)));
    } catch (err) {
      console.error('Failed to load replies', err);
    }
  };

  const handleToggleReplies = (postId: string) => {
    const isExpanded = !!expandedPosts[postId];
    setExpandedPosts({ ...expandedPosts, [postId]: !isExpanded });
    
    // Only fetch replies if expanding and hasn't been fetched yet
    if (!isExpanded) {
      handleLoadReplies(postId);
    }
  };

  const sortPostsArray = (postArray: Post[]) => {
    const copy = [...postArray];
    copy.sort((a, b) => {
      const timeA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
      const timeB = b.createdAt ? new Date(b.createdAt).getTime() : 0;

      if (sort === 'trending') {
        const netA = (a.upvotes || 0) - (a.downvotes || 0);
        const netB = (b.upvotes || 0) - (b.downvotes || 0);
        const scoreA = netA + (a.views || 0) + (a.replyCount || 0);
        const scoreB = netB + (b.views || 0) + (b.replyCount || 0);
        
        if (scoreB !== scoreA) return scoreB - scoreA;
        return timeB - timeA; // Tie-breaker: newest
      } 
      
      if (sort === 'newest') {
        return timeB - timeA;
      }
      
      // Default strict net-score fallback
      const netA = (a.upvotes || 0) - (a.downvotes || 0);
      const netB = (b.upvotes || 0) - (b.downvotes || 0);
      if (netB !== netA) return netB - netA;
      
      return timeB - timeA;
    });
    return copy;
  };

  const handleVote = async (postId: string, action: 'upvote' | 'downvote') => {
    // 1. Optimistic Update (Immediate UI response)
    const targetPost = posts.find(p => p.id === postId);
    if (!targetPost) return;

    const optPost = { ...targetPost };
    const currentUserId = user?.id || '';
    
    if (action === 'upvote') {
      if (!optPost.upvotedBy) optPost.upvotedBy = [];
      if (!optPost.downvotedBy) optPost.downvotedBy = [];
      if (!optPost.upvotedBy.includes(currentUserId)) {
        optPost.upvotedBy.push(currentUserId);
        optPost.upvotes = (optPost.upvotes || 0) + 1;
        if (optPost.downvotedBy.includes(currentUserId)) {
          optPost.downvotedBy = optPost.downvotedBy.filter(id => id !== currentUserId);
          optPost.downvotes = Math.max(0, (optPost.downvotes || 0) - 1);
        }
      }
    } else {
      if (!optPost.downvotedBy) optPost.downvotedBy = [];
      if (!optPost.upvotedBy) optPost.upvotedBy = [];
      if (!optPost.downvotedBy.includes(currentUserId)) {
        optPost.downvotedBy.push(currentUserId);
        optPost.downvotes = (optPost.downvotes || 0) + 1;
        if (optPost.upvotedBy.includes(currentUserId)) {
          optPost.upvotedBy = optPost.upvotedBy.filter(id => id !== currentUserId);
          optPost.upvotes = Math.max(0, (optPost.upvotes || 0) - 1);
        }
      }
    }

    // Update UI instantly
    setPosts(sortPostsArray(posts.map(p => p.id === postId ? optPost : p)));

    // 2. Perform Backend Call
    try {
      const updatedPost = await actOnPost(postId, action);
      setPosts(currentPosts => sortPostsArray(currentPosts.map(p => p.id === postId ? updatedPost : p)));
    } catch (err: any) {
      console.error('Failed to act on post', err);
      // Revert if API fails
      setPosts(sortPostsArray(posts)); 
    }
  };

  const handleQuickReply = (postId: string) => {
    if (!expandedPosts[postId]) {
      handleToggleReplies(postId);
    }
    setTimeout(() => {
      replyInputRefs.current[postId]?.focus();
    }, 50);
  };

  const renderAuthorName = (name?: string, id?: string) => {
    const author = name || id || 'Unknown User';
    // If it looks like a UUID (long string with hyphens), show a fallback format
    if (author.length > 20 && author.includes('-')) {
      return `User ${author.substring(0, 6)}`;
    }
    return author;
  };

  return (
    <div className="forum-layout-container">
      <div className="forum-header-row" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <header className="page-hero" style={{ margin: 0 }}>
          <h1 className="forum-header-title">Forum</h1>
          <p className="forum-header-subtitle" style={{ marginBottom: 0 }}>{posts.length} threads</p>
        </header>

        <button className="start-discussion-btn" style={{ width: 'auto', padding: '0.5rem 1.5rem', margin: 0 }} onClick={() => setIsCreateModalOpen(true)}>
          Start discussion
        </button>
      </div>

      <div className="forum-grid">
        <div className="post-feed-column">

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        {posts.map((post) => (
          <div key={post.id} className="forum-post-card">
            <div className="post-header">
              <div className="post-avatar">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
              </div>
              <div className="post-meta-info">
                <span className="post-author">{renderAuthorName(post.authorName, post.authorId)}</span>
                <span className="post-time">{new Date(post.createdAt || Date.now()).toLocaleDateString()}</span>
              </div>
            </div>

            <div className="post-title">{post.title}</div>
            <div className="post-body">
              {post.body}
              {post.body && post.body.length > 100 && <span className="post-see-more">see more</span>}
            </div>

            <div className="post-actions">
              <div className={`action-item ${post.upvotedBy?.includes(user?.id || '') ? 'action-item-active' : 'action-item-muted'}`} onClick={() => handleVote(post.id, 'upvote')} title="Upvote">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="19" x2="12" y2="5"></line><polyline points="5 12 12 5 19 12"></polyline></svg>
                {post.upvotes || 0}
              </div>
              <div className={`action-item ${post.downvotedBy?.includes(user?.id || '') ? 'action-item-active-down' : 'action-item-muted'}`} onClick={() => handleVote(post.id, 'downvote')} title="Downvote">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg>
                {post.downvotes || 0}
              </div>
              <div className="action-item action-item-muted" onClick={() => handleToggleReplies(post.id)} style={{ cursor: 'pointer', marginLeft: '0.5rem' }} title="Comments">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                {post.replyCount || 0}
              </div>
              <div className="action-spacer"></div>
              <div ref={activeMenuPostId === post.id ? menuRef : null} style={{ position: 'relative', display: 'inline-block' }}>
                <div className="action-item action-item-muted" style={{ marginLeft: '0.5rem', cursor: 'pointer' }} onClick={() => setActiveMenuPostId(activeMenuPostId === post.id ? null : post.id)}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="1"></circle><circle cx="12" cy="5" r="1"></circle><circle cx="12" cy="19" r="1"></circle></svg>
                </div>
                {activeMenuPostId === post.id && (
                  <div className="post-options-menu">
                    <div className="post-options-item" onClick={() => handleOpenReportModal(post.id)}>
                      Report Thread
                    </div>
                    {user?.id === post.authorId && (
                      <div className="post-options-item" style={{ color: 'var(--danger-color, #ef4444)' }} onClick={() => handleDeletePost(post.id)}>
                        Delete Post
                      </div>
                    )}
                  </div>
                )}
              </div>

            </div>

            {/* Replies & Input - Only visible if expanded */}
            {expandedPosts[post.id] && (
              <>
                {post.replies && post.replies.length > 0 && (
                  <div className="post-reply-section">
                    {post.replies.map((r, i) => (
                      <div key={i} className="reply-item">
                        <div className="reply-author">{renderAuthorName(r.authorName, r.authorId)}</div>
                        <div className="reply-body">{r.body}</div>
                      </div>
                    ))}
                  </div>
                )}
                
                <div className="reply-input-wrapper">
                  <input
                    ref={(el) => { replyInputRefs.current[post.id] = el; }}
                    placeholder="Write a reply..."
                    value={replyText[post.id] || ''}
                    onChange={(e) => setReplyText({ ...replyText, [post.id]: e.target.value })}
                    onKeyDown={(e) => { if (e.key === 'Enter') handleAddReply(post.id); }}
                  />
                  <button className="reply-btn" onClick={() => handleAddReply(post.id)}>Reply</button>
                </div>
              </>
            )}
          </div>
        ))}
          </div>
        </div>


      </div>

      {isCreateModalOpen && (
        <div className="create-post-modal-overlay" onClick={() => setIsCreateModalOpen(false)}>
          <div className="create-post-modal-content" role="dialog" aria-modal="true" aria-labelledby="create-post-modal-title" onClick={e => e.stopPropagation()}>
            <button type="button" className="modal-close-btn" aria-label="Close" onClick={() => setIsCreateModalOpen(false)}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
            <h2 id="create-post-modal-title" style={{ color: 'var(--text-main)', marginBottom: '1.5rem', fontSize: '1.5rem', fontWeight: 600 }}>Start a Discussion</h2>
            <form onSubmit={(e) => { handleCreatePost(e); setIsCreateModalOpen(false); }}>
              <Input label="Title" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="e.g. Pest control strategies for tomatoes" required />
              <Input label="Tags (comma-separated)" value={tags} onChange={(e) => setTags(e.target.value)} placeholder="e.g. pest-control, greenhouse, tomatoes" />
              <div style={{ marginBottom: '1.5rem', position: 'relative' }}>
                <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)', fontWeight: 500 }}>Discussion Content</label>
                <div style={{ position: 'relative' }}>
                  <textarea
                    className="forum-modal-textarea"
                    rows={5}
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    placeholder="Write your question or experience..."
                    required
                  />
                  {/* Floating Icon inside textarea */}
                  <div style={{ position: 'absolute', top: '0.75rem', right: '0.75rem', color: 'var(--accent-green)', pointerEvents: 'none' }}>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                    </svg>
                  </div>
                </div>
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1rem' }}>
                <button type="button" className="reply-btn" style={{ background: 'transparent', border: '1px solid var(--border-color)', color: 'var(--text-main)', padding: '0.6rem 1.5rem', borderRadius: '8px' }} onClick={() => setIsCreateModalOpen(false)}>Cancel</button>
                <button type="submit" className="post-forum-btn">Post to Forum</button>
              </div>
            </form>
          </div>
        </div>
      )}
      {isReportModalOpen && (
        <div className="create-post-modal-overlay" onClick={() => setIsReportModalOpen(false)}>
          <div className="create-post-modal-content report-modal-content" role="dialog" aria-modal="true" aria-labelledby="report-modal-title" onClick={e => e.stopPropagation()}>
            <button type="button" className="modal-close-btn" aria-label="Close" onClick={() => setIsReportModalOpen(false)}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
            <h2 id="report-modal-title" style={{ color: 'var(--text-main)', marginBottom: '1.5rem', fontSize: '1.5rem', fontWeight: 600 }}>Report Thread</h2>
            <form onSubmit={handleSubmitReport}>
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '1rem', fontWeight: 500, color: 'var(--text-main)', fontSize: '1.05rem' }}>Select a reason for reporting</label>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {['SPAM', 'INAPPROPRIATE', 'HARASSMENT'].map(type => (
                    <label key={type} className="report-radio-option" style={{ borderColor: reportType === type ? 'var(--accent-green)' : 'var(--border-color)', background: reportType === type ? 'rgba(46, 204, 113, 0.05)' : 'var(--bg-input)' }}>
                      <input type="radio" name="reportType" value={type} checked={reportType === type} onChange={(e) => setReportType(e.target.value)} />
                      <span>{type.charAt(0) + type.slice(1).toLowerCase()}</span>
                    </label>
                  ))}
                </div>
              </div>
              <div className="report-modal-actions">
                <button type="button" className="reply-btn" style={{ background: 'transparent', border: '1px solid var(--border-color)', color: 'var(--text-main)', padding: '0.6rem 1.5rem', borderRadius: '8px' }} onClick={() => setIsReportModalOpen(false)}>Cancel</button>
                <button type="submit" className="reply-btn" style={{ background: 'var(--danger-color, #ef4444)', border: 'none', padding: '0.6rem 1.5rem', borderRadius: '8px', color: '#fff', fontWeight: 600 }}>Submit Report</button>
              </div>
            </form>
          </div>
        </div>
      )}
      
      {deleteConfirmPostId && (
        <div className="create-post-modal-overlay" onClick={() => setDeleteConfirmPostId(null)}>
          <div className="create-post-modal-content report-modal-content" role="dialog" aria-modal="true" onClick={e => e.stopPropagation()}>
            <button type="button" className="modal-close-btn" aria-label="Close" onClick={() => setDeleteConfirmPostId(null)}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
            <h2 style={{ color: 'var(--text-main)', marginBottom: '1rem', fontSize: '1.25rem', fontWeight: 600 }}>Delete Post</h2>
            <p style={{ color: 'var(--text-main)', marginBottom: '2rem' }}>Are you sure you want to delete this post? This action cannot be undone.</p>
            <div className="report-modal-actions">
              <button type="button" className="reply-btn" style={{ background: 'transparent', border: '1px solid var(--border-color)', color: 'var(--text-main)', padding: '0.6rem 1.5rem', borderRadius: '8px' }} onClick={() => setDeleteConfirmPostId(null)}>Cancel</button>
              <button type="button" className="reply-btn" style={{ background: 'var(--danger-color, #ef4444)', border: 'none', padding: '0.6rem 1.5rem', borderRadius: '8px', color: '#fff', fontWeight: 600 }} onClick={executeDelete}>Delete</button>
            </div>
          </div>
        </div>
      )}

      {toast && (
        <div className={`toast-notification toast-${toast.type}`}>
          {toast.message}
        </div>
      )}
    </div>
  );
};
