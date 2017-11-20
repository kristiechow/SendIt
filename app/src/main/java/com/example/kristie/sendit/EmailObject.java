package com.example.kristie.sendit;

/**
 * Created by Kristie on 11/19/17.
 */

public class EmailObject {

    public String Contact;
    public String Subject;
    public String Body;

    public EmailObject() {
    }

    public EmailObject(String sContact, String sSubject, String sBody) {
        this.Contact = Contact;
        this.Subject = Subject;
        this.Body = Body;
    }

    public String getsContact() {
        return Contact;
    }

    public void setsContact(String sContact) {
        this.Contact = Contact;
    }

    public String getsSubject() {
        return Subject;
    }

    public void setsSubject(String sSubject) {
        this.Subject = Subject;
    }

    public String getsBody() {
        return Body;
    }

    public void setsBody(String sBody) {
        this.Body = sBody;
    }
}
