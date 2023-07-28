package com.example.testproject;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class FAQ {
    private SimpleIntegerProperty id;
    private SimpleStringProperty question;
    private SimpleStringProperty answer;

    public FAQ(int id, String question, String answer) {
        this.id = new SimpleIntegerProperty(id);
        this.question = new SimpleStringProperty(question);
        this.answer = new SimpleStringProperty(answer);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getQuestion() {
        return question.get();
    }

    public SimpleStringProperty questionProperty() {
        return question;
    }

    public void setQuestion(String question) {
        this.question.set(question);
    }

    public String getAnswer() {
        return answer.get();
    }

    public SimpleStringProperty answerProperty() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer.set(answer);
    }
}
