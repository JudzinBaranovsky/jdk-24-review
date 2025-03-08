package org.bananalaba.jdk24;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    @NonNull
    private final MessageStore messageStore;

    @PostMapping
    public void upsert(final @RequestParam String key, final @RequestParam String text) {
        messageStore.putMessage(new Message(key, text));
    }

    @GetMapping
    public Message get(final @RequestParam String key) {
        return messageStore.getMessage(key);
    }

    @DeleteMapping
    public void delete(final @RequestParam String key) {
        messageStore.deleteMessage(key);
    }

}
