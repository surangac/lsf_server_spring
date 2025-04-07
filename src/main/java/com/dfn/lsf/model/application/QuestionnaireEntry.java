package com.dfn.lsf.model.application;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionnaireEntry {
    private int questionNumber;
    private int answerNumber;
    private String description;

    public HashMap<String, Object> getAttributeMap() {
        HashMap<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("pm06_question_number", questionNumber);
        attributeMap.put("pm06_configured_answer", answerNumber);
        attributeMap.put("pm06_content", description);
        return attributeMap;
    }
}
