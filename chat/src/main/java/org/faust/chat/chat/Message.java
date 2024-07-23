package org.faust.chat.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message(UUID id, UUID channelId, String sender, String message, @JsonDeserialize(using = LocalDateTimeDeserializer.class)
@JsonFormat(pattern="dd/MM/yyyy HH:mm") LocalDateTime serverTime, @JsonDeserialize(using = LocalDateTimeDeserializer.class)
@JsonFormat(pattern="dd/MM/yyyy HH:mm") LocalDateTime editTime, UUID senderId) {
}
