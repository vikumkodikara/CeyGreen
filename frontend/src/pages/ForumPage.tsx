import React, { useState, useEffect, useRef } from 'react';
import { listPosts, createPost, addReply, getPost, actOnPost, deletePost, reportPost, askAi, getAiInsights } from '../api/forum';
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
  const [replyText, setReplyText] = useState<Record<string, string>>({});
  const [expandedPosts, setExpandedPosts] = useState<Record<string, boolean>>({});
  const replyInputRefs = useRef<Record<string, HTMLInputElement | null>>({});

  const [activeMenuPostId, setActiveMenuPostId] = useState<string | null>(null);

  // AI Chat States
  const [aiMessage, setAiMessage] = useState('');
  const [aiHistory, setAiHistory] = useState<{role: string, content: string}[]>([]);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiInsights, setAiInsights] = useState<string[]>(['Climate control automation tips', 'Tomato blight identification', 'New fertilizer trends']);
  const chatHistoryRef = useRef<HTMLDivElement>(null);
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
      alert('Post reported for review.');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to report post');
    } finally {
      setIsReportModalOpen(false);
      setReportPostId(null);
    }
  };

  const handleDeletePost = async (postId: string) => {
    if (!window.confirm('Are you sure you want to delete this post?')) return;
    try {
      await deletePost(postId);
      fetchPosts();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to delete post');
    }
    setActiveMenuPostId(null);
  };

  useEffect(() => {
    fetchPosts();
    fetchInsights();
  }, [sort]);

  const fetchInsights = async () => {
    try {
      const insights = await getAiInsights('General greenhouse farming');
      setAiInsights(insights);
    } catch {
      setAiInsights(['Climate control automation', 'Tomato blight identification', 'New fertilizer trends']);
    }
  };

  useEffect(() => {
    if (chatHistoryRef.current) {
      chatHistoryRef.current.scrollTop = chatHistoryRef.current.scrollHeight;
    }
  }, [aiHistory]);

  const handleAiSubmit = async () => {
    if (!aiMessage.trim() || aiLoading) return;
    
    const userMsg = aiMessage.trim();
    setAiMessage('');
    setAiHistory(prev => [...prev, { role: 'user', content: userMsg }]);
    setAiLoading(true);
    
    try {
      const response = await askAi(userMsg, aiHistory);
      setAiHistory(prev => [...prev, { role: 'assistant', content: response }]);
    } catch (err) {
      setAiHistory(prev => [...prev, { role: 'assistant', content: 'Sorry, I am having trouble connecting right now.' }]);
    } finally {
      setAiLoading(false);
    }
  };

  const handleAiKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleAiSubmit();
    }
  };

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
      setPosts(sortPostsArray(posts.map(p => p.id === postId ? updatedPost : p)));
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to post reply');
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
      <div className="forum-grid">
        <div className="post-feed-column">
          <header className="page-hero">
            <h1 className="forum-header-title">Forum</h1>
            <p className="forum-header-subtitle">{posts.length} threads</p>
          </header>

          <button className="start-discussion-btn" onClick={() => setIsCreateModalOpen(true)}>
            Start discussion
          </button>

          <h2 className="trending-header">Threads</h2>

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
              <div style={{ position: 'relative', display: 'inline-block' }}>
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
              <div className="action-spacer"></div>
              <div className="action-item action-item-muted">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                {post.views || 0}
              </div>
              <div className="action-item action-item-muted" onClick={() => handleToggleReplies(post.id)} style={{ cursor: 'pointer' }} title="Toggle replies">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                {post.replyCount || 0}
              </div>
              <div className="action-item action-item-muted" onClick={() => handleQuickReply(post.id)} style={{ cursor: 'pointer', marginLeft: '0.25rem' }} title="Quick Reply">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="9 17 4 12 9 7"></polyline><path d="M20 18v-2a4 4 0 0 0-4-4H4"></path></svg>
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

        <div className="sidebar-column">
          <div className="ai-sidebar-panel">
            <h3 className="ai-sidebar-title">AI Green Assistant</h3>
            <p className="ai-sidebar-desc">Ask me about diagnostics, climate control or market data.</p>
            
            <div className="ai-sidebar-logo-wrapper">
              <svg className="ai-sidebar-logo" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M9.5 2A2.5 2.5 0 0 1 12 4.5v15a2.5 2.5 0 0 1-4.96.44 2.5 2.5 0 0 1-2.96-3.08 3 3 0 0 1-.34-5.58 2.5 2.5 0 0 1 1.32-4.24 2.5 2.5 0 0 1 1.98-3A2.5 2.5 0 0 1 9.5 2Z"></path>
                <path d="M14.5 2A2.5 2.5 0 0 0 12 4.5v15a2.5 2.5 0 0 0 4.96.44 2.5 2.5 0 0 0 2.96-3.08 3 3 0 0 0 .34-5.58 2.5 2.5 0 0 0-1.32-4.24 2.5 2.5 0 0 0-1.98-3A2.5 2.5 0 0 0 14.5 2Z"></path>
                <path d="M14 20c2 1 4-1 4-3"></path>
                <path d="M10 20c-2 1-4-1-4-3"></path>
                <text x="12" y="14" fontSize="5" fontWeight="bold" textAnchor="middle" fill="currentColor" stroke="none">AI</text>
              </svg>
            </div>

            {aiHistory.length > 0 && (
              <div className="ai-chat-history" ref={chatHistoryRef}>
                {aiHistory.map((msg, idx) => (
                  <div key={idx} className={`ai-message-bubble ${msg.role === 'user' ? 'ai-message-user' : 'ai-message-bot'}`}>
                    {msg.content}
                  </div>
                ))}
                {aiLoading && (
                  <div className="ai-message-bubble ai-message-bot" style={{ opacity: 0.7 }}>
                    Thinking...
                  </div>
                )}
              </div>
            )}

            <div className="ai-input-wrapper">
              <textarea 
                className="ai-sidebar-input" 
                placeholder="Ask a question..." 
                rows={1}
                value={aiMessage}
                onChange={(e) => setAiMessage(e.target.value)}
                onKeyDown={handleAiKeyDown}
              ></textarea>
              <button 
                className="ai-send-btn" 
                onClick={handleAiSubmit}
                disabled={!aiMessage.trim() || aiLoading}
                aria-label="Send message"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <line x1="22" y1="2" x2="11" y2="13"></line>
                  <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                </svg>
              </button>
            </div>

            <h4 className="ai-insights-title">AI Insights</h4>
            <div className="ai-insights-list">
              {aiInsights.map((insight, idx) => (
                <button 
                  key={idx} 
                  className="ai-insight-pill"
                  onClick={() => {
                    setAiMessage(insight);
                    // Focus logic could go here
                  }}
                >
                  {insight}
                </button>
              ))}
            </div>
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
              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', marginBottom: '0.4rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>Discussion Content</label>
                <textarea
                  className="ai-sidebar-input"
                  style={{ marginBottom: 0 }}
                  rows={5}
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="Write your question or experience..."
                  required
                />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <button type="submit" className="reply-btn" style={{ padding: '0.75rem 1.5rem', fontSize: '1rem' }}>Post to Forum</button>
              </div>
            </form>
          </div>
        </div>
      )}
      {isReportModalOpen && (
        <div className="create-post-modal-overlay" onClick={() => setIsReportModalOpen(false)}>
          <div className="create-post-modal-content" role="dialog" aria-modal="true" aria-labelledby="report-modal-title" onClick={e => e.stopPropagation()}>
            <button type="button" className="modal-close-btn" aria-label="Close" onClick={() => setIsReportModalOpen(false)}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
            <h2 id="report-modal-title" style={{ color: 'var(--text-main)', marginBottom: '1.5rem', fontSize: '1.5rem', fontWeight: 600 }}>Report Thread</h2>
            <form onSubmit={handleSubmitReport}>
              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>Select Reason</label>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {['SPAM', 'INAPPROPRIATE', 'HARASSMENT'].map(type => (
                    <label key={type} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
                      <input type="radio" name="reportType" value={type} checked={reportType === type} onChange={(e) => setReportType(e.target.value)} />
                      {type}
                    </label>
                  ))}
                </div>
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
                <button type="button" className="reply-btn" style={{ background: 'transparent', border: '1px solid var(--border-color)', color: 'var(--text-main)' }} onClick={() => setIsReportModalOpen(false)}>Cancel</button>
                <button type="submit" className="reply-btn" style={{ background: 'var(--danger-color, #ef4444)' }}>Submit Report</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
