package cc.blynk.server.handlers.http.helpers.pojo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 28.12.15.
 */
public class PushMessagePojo {

    public String body;

    public PushMessagePojo() {
    }

    public PushMessagePojo(String body) {
        this.body = body;
    }

}
