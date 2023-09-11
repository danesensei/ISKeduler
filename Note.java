package com.example.iskeduler;

public class Note {
    String key, Title, Content;

    public Note() {

    }

    public String getTitle() {
        return Title;
    }

    public String getContent() {
        return Content;
    }

    public String getKey() {
        return key;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

