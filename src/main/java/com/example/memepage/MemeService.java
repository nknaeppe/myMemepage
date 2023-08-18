package com.example.memepage;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class MemeService {
    private final MemeRepository memeRepository;

    @Autowired
    public MemeService(MemeRepository memeRepository) {
        this.memeRepository = memeRepository;
    }

    public Optional<Meme> getMeme(String memeId) {
        return memeRepository.findById(new ObjectId(memeId));
    }

    public List<Meme> getAllMemes() {
        return memeRepository.findAll();
    }

    public List<Meme> getNewMemes() {
        return memeRepository.findAll(Sort.by(Sort.Direction.ASC, "createdDate"));
    }

    public void upVoteMeme(String memeId, String userId) {
        Meme meme = findMeme(memeId);
        meme.upvote(userId);
        memeRepository.save(meme);
    }

    public void downVoteMeme(String memeId, String userId) {
        Meme meme = findMeme(memeId);
        meme.downVote(userId);
        memeRepository.save(meme);
    }

    private Meme findMeme(String memeId) {
        ObjectId id = new ObjectId(memeId);
        Optional<Meme> optionalMeme = memeRepository.findById(id);
        return optionalMeme.orElseThrow(IllegalArgumentException::new);
    }


    public Meme uploadMeme(String userId, String username, String title, MultipartFile file) throws IOException {
        Meme meme = new Meme();
        meme.setUserId(userId);
        meme.setUsername(username);
        meme.setTitle(title);
        meme.setCreatedDate(LocalDateTime.now());
        meme.setUpVotedUsers(new HashSet<>());
        meme.setDownVotedUsers(new HashSet<>());
        Binary image = new Binary(file.getBytes());
        meme.setImage(image);

        return memeRepository.save(meme);
    }

}
