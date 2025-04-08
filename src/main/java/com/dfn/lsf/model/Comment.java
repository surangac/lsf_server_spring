package com.dfn.lsf.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Comment {  
    private String commentID;
    private String reversedFrom;
    private String reversedTo;
    private String comment;
    private Date timeStamp;
    private String parentID;
    private String commentedBy;
    private Comment reply;
}