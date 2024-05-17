package com.ssssogong.issuemanager.domain;

import com.ssssogong.issuemanager.domain.account.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;


    public void setIssue(Issue issue) {
        if (this.issue != null) {
            this.issue.getComments().remove(this);
        }
        this.issue = issue;
        issue.getComments().add(this);
    }

    public void update(String content, List<String> imageUrls) {
        this.content = content;
        this.imageUrls = imageUrls;
    }
}
