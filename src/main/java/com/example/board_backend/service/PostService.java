package com.example.board_backend.service;

import com.example.board_backend.dto.Member;
import com.example.board_backend.dto.Post;
import com.example.board_backend.dto.request.PostCreateRequest;
import com.example.board_backend.dto.request.PostUpdateRequest;
import com.example.board_backend.dto.response.PostResponse;
import com.example.board_backend.exception.ForbiddenException;
import com.example.board_backend.exception.NotFoundException;
import com.example.board_backend.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PostService {

    private final PostMapper postMapper;

    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public List<PostResponse> getPosts(String keyword, int size, int offset) {
        return postMapper.findAll(keyword, size, offset).stream()
                .map(PostResponse::from)
                .toList();
    }

    public int getTotalPages(String keyword, int size) {
        int totalCount = postMapper.countPosts(keyword);
        if (totalCount == 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalCount / size);
    }

    @Transactional
    public PostResponse getPostAndIncreaseViews(Long id) {
        if (postMapper.increaseViews(id) == 0) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. id=" + id);
        }
        return PostResponse.from(postMapper.findById(id));
    }

    // ★ 작성자는 요청이 아니라 로그인한 회원으로 정한다
    public void createPost(Member member, PostCreateRequest request) {
        Post post = new Post();
        post.setMemberId(member.getId());
        post.setAuthor(member.getNickname());
        post.setTitle(request.title().trim());
        post.setContent(request.content());
        postMapper.insertPost(post);
    }

    @Transactional
    public PostResponse updatePost(Long id, Long loginMemberId, PostUpdateRequest request) {
        findOwnedPost(id, loginMemberId);   // ★ 없으면 404, 남의 글이면 403

        Post post = new Post();
        post.setId(id);
        post.setTitle(request.title().trim());
        post.setContent(request.content());
        postMapper.updatePost(post);

        return PostResponse.from(postMapper.findById(id));
    }

    public void deletePost(Long id, Long loginMemberId) {
        findOwnedPost(id, loginMemberId);   // ★ 없으면 404, 남의 글이면 403
        postMapper.deletePost(id);
    }

    // 글을 찾고, 로그인한 회원이 작성자인지 확인한다
    private Post findOwnedPost(Long id, Long loginMemberId) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. id=" + id);
        }
        if (!Objects.equals(post.getMemberId(), loginMemberId)) {
            throw new ForbiddenException("본인이 작성한 글만 수정/삭제할 수 있습니다.");
        }
        return post;
    }
}