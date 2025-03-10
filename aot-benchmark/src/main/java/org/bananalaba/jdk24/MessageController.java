package org.bananalaba.jdk24;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/{key}")
    public void upsert(final @PathVariable String key, final @RequestParam String text) {
        messageStore.putMessage(new Message(key, text));
    }

    @GetMapping(value = "/{key}", produces = "application/json")
    public Message get(final @PathVariable String key) {
        return messageStore.getMessage(key);
    }

    @DeleteMapping("/{key}")
    public void delete(final @PathVariable String key) {
        messageStore.deleteMessage(key);
    }

}
