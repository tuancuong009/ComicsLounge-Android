package com.comics.lounge.modals;

public class QA {
    String id, ques, awns;
    boolean isHidden;

    public QA(String id, String ques, String awns, boolean isHidden) {
        this.id = id;
        this.ques = ques;
        this.awns = awns;
        this.isHidden = isHidden;
    }

    public String getAwns() {
        return awns;
    }

    public void setAwns(String awns) {
        this.awns = awns;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QA(){}

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
