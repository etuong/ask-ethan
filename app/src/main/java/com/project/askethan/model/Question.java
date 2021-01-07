package com.project.askethan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Question {
    private long id;
    private int views;
    private String title;
    private String authorUid;
    private String authorName;
    private String question;
}
