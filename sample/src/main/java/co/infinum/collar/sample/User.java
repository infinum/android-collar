package co.infinum.collar.sample;

import java.util.HashMap;
import java.util.Map;

import co.infinum.collar.Trackable;

public class User implements Trackable {

    final String name;
    final String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public Map<String, Object> getTrackableAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", name);
        return attributes;
    }
}
