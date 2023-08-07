package com.example.memepage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Set;

@Document("memes")
@Getter
@Setter
@ToString
public class Meme {
    @Id
    private ObjectId id;
    @CreatedDate
    private LocalDateTime createdDate;
    private String userId;
    private String title;
    private Binary image;
    private Set<String> upVotedUsers;
    private Set<String> downVotedUsers;

    public void upvote(String userId) {
        downVotedUsers.remove(userId);
        upVotedUsers.add(userId);
    }

    public void downVote(String userId) {
        upVotedUsers.remove(userId);
        downVotedUsers.add(userId);
    }

    public int getUpvotes() {
        if (upVotedUsers == null) {
            return 0;
        }
        return upVotedUsers.size();
    }

    public int getDownVotes() {
        if (downVotedUsers == null) {
            return 0;
        }
        return downVotedUsers.size();
    }

    public int getScore() {
        return getUpvotes() - getDownVotes();
    }

    public String getBase64Image() {
        return Base64.getEncoder().encodeToString(this.image.getData());
    }
}
