package com.ssssogong.issuemanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public CommentImage(Long id, String imageUrl, Comment comment) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.comment = comment;
    }

    public void setComment(Comment comment) {
        if (this.comment != null) {
            this.comment.getCommentImages().remove(this);
        }
        this.comment = comment;
        comment.getCommentImages().add(this);
    }
}
