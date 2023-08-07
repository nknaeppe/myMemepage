package com.example.memepage;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        Optional<Meme> optionalMeme = memeRepository.findById(new ObjectId(memeId));
        Meme meme = optionalMeme.orElseThrow(IllegalArgumentException::new);
        meme.upvote(userId);
        memeRepository.save(meme);
    }

    public void downVoteMeme(String memeId, String userId) {
        Optional<Meme> optionalMeme = memeRepository.findById(new ObjectId(memeId));
        Meme meme = optionalMeme.orElseThrow(IllegalArgumentException::new);
        meme.downVote(userId);
        memeRepository.save(meme);
    }


    public Meme uploadMeme(String userId, String title, MultipartFile file) throws IOException {
        Meme meme = new Meme();
        meme.setUserId(userId);
        meme.setTitle(title);
        meme.setUpVotedUsers(new HashSet<>());
        meme.setDownVotedUsers(new HashSet<>());
        Binary image = new Binary(file.getBytes());
        meme.setImage(image);

        return memeRepository.save(meme);
    }

}
