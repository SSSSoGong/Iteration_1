package com.ssssogong.issuemanager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IssueUpdateRequestDto {

    private String title;
    private String description;
    private String priority;

    @Builder
    public IssueUpdateRequestDto(String title, String description, String priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
}
