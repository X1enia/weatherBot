package com.example.subscribeservice.common;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("users")
public class User {
    @Id
    private String id;
    private String telegramId;
    private String userName;
    private String firstName;
    private String lastName;
    private LocalDateTime regDate;
}
