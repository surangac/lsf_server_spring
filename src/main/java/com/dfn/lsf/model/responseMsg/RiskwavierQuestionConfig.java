package com.dfn.lsf.model.responseMsg;

/**
 * Created by manodyas on 3/15/2016.
 */
public class RiskwavierQuestionConfig {
    private int questionNumber;
    private String configureState;
    private String content;

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getConfigureState() {
        return configureState;
    }

    public void setConfigureState(String configureState) {
        this.configureState = configureState;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
