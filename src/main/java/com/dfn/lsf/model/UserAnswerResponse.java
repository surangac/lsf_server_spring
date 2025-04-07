package com.dfn.lsf.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by manodyas on 8/26/2015.
 */
public class UserAnswerResponse {
    private Map<String, String> answers;

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
}
