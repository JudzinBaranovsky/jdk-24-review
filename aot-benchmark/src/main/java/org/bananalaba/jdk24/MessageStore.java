package org.bananalaba.jdk24;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import static org.springframework.util.Assert.notNull;

@Component
public class MessageStore {

    private final Map<String, Message> messages = new ConcurrentHashMap<>();

    public Message getMessage(final @NonNull String key) {
        var message = messages.get(key);

        if (message == null) {
            throw new DataNotFoundException("message not found for key=" + key);
        }

        return message;
    }

    public void putMessage(final @NonNull Message message) {
        messages.put(message.getKey(), message);
    }

    public void deleteMessage(final @NonNull String key) {
        var removed = messages.remove(key);
        notNull(removed, "message not found");
    }

}
