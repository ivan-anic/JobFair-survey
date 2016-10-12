package com.kset.jobfair.ianic.jobfairpoll.objects;

/**
 * An object model which represents a single question, which keeps the following data:
 * <ul>
 *     <li>question id</li>
 *     <li>question text</li>
 *     <li>the time when the question was created</li>
 *     <li>the time when the question was last modified</li>
 * </ul>
 *
 * @author Ivan Anić
 * @version 1.0
 */
public class Question {

    private int id;
    private String text;
    private String created_at;
    private String updated_at;


    public Question(int id, String text, String created_at, String updated_at) {
        this.updated_at = updated_at;
        this.created_at = created_at;
        this.text = text;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
