package com.example.memepage;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class MemeServiceTest {

    @Mock
    private MemeRepository memeRepository;

    @InjectMocks
    private MemeService memeService;

    @Test
    void testGetMeme() {
        String memeId = "64d1331c5c4ebf345206b385";
        Meme mockMeme = new Meme();
        when(memeRepository.findById(new ObjectId(memeId))).thenReturn(Optional.of(mockMeme));

        Optional<Meme> result = memeService.getMeme(memeId);

        assertTrue(result.isPresent());
        assertEquals(mockMeme, result.get());
    }

    @Test
    void testGetAllMemes() {
        ArrayList<Meme> memes = new ArrayList<>();
        Collections.addAll(memes, new Meme(), new Meme(), new Meme());

        when(memeRepository.findAll()).thenReturn(memes);

        List<Meme> allMemes = memeRepository.findAll();

        assertEquals(3, allMemes.size());
    }

    @Test
    void testGetNewMemes() {
        Meme meme1 = new Meme();
        meme1.setCreatedDate(LocalDateTime.parse("2023-03-01T10:00:00"));
        Meme meme2 = new Meme();
        meme2.setCreatedDate(LocalDateTime.parse("2023-02-01T10:00:00"));
        Meme meme3 = new Meme();
        meme3.setCreatedDate(LocalDateTime.parse("2023-01-01T10:00:00"));

        ArrayList<Meme> mockMemes = new ArrayList<>();
        Collections.addAll(mockMemes, meme1, meme2, meme3);

        when(memeRepository.findAll(Sort.by(Sort.Direction.ASC, "createdDate"))).thenReturn(mockMemes);

        List<Meme> result = memeService.getTopMemes();

        assertEquals(3, result.size());
        assertEquals(meme1, result.get(0));
        assertEquals(meme2, result.get(1));
        assertEquals(meme3, result.get(2));
    }

    @Test
    void testUpVoteMeme() {
        Meme mockMeme = new Meme();
        String memeId = "64d1331c5c4ebf345206b385";
        mockMeme.setDownVotedUsers(new HashSet<>());
        mockMeme.setUpVotedUsers(new HashSet<>());
        String uuid = "b85d5822-2837-4a1e-a395-fdfb9555cb27";

        when(memeRepository.findById(new ObjectId(memeId))).thenReturn(Optional.of(mockMeme));
        when(memeRepository.save(mockMeme)).thenReturn(mockMeme);
        memeService.upVoteMeme(memeId, uuid);
        when(memeRepository.findById(new ObjectId(memeId))).thenReturn(Optional.of(mockMeme));
        memeService.getMeme(memeId);

        assertEquals(1, mockMeme.getUpvotes());
        assertTrue(mockMeme.getUpVotedUsers().contains(uuid));
    }

    @Test
    void testDownVoteMeme() {
        Meme mockMeme = new Meme();
        String memeId = "64d1331c5c4ebf345206b385";
        mockMeme.setDownVotedUsers(new HashSet<>());
        mockMeme.setUpVotedUsers(new HashSet<>());
        String uuid = "b85d5822-2837-4a1e-a395-fdfb9555cb27";

        when(memeRepository.findById(new ObjectId(memeId))).thenReturn(Optional.of(mockMeme));
        when(memeRepository.save(mockMeme)).thenReturn(mockMeme);

        memeService.downVoteMeme(memeId, uuid);
        when(memeRepository.findById(new ObjectId(memeId))).thenReturn(Optional.of(mockMeme));
        memeService.getMeme(memeId);

        assertEquals(1, mockMeme.getDownVotes());
        assertTrue(mockMeme.getDownVotedUsers().contains(uuid));
    }


    @Test
    void testUploadMeme() throws IOException {
        String userId = "b85d5822-2837-4a1e-a395-fdfb9555cb27";
        String username = "bibo";
        ObjectId memeId = new ObjectId("64d1331c5c4ebf345206b385");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        Meme newMeme = new Meme();
        newMeme.setId(memeId);
        newMeme.setUsername(username);
        newMeme.setUserId(userId);
        newMeme.setTitle("random Title");
        Binary image = new Binary(file.getBytes());
        newMeme.setImage(image);
        when(memeRepository.save(any())).thenReturn(newMeme);

        Meme uploadedMeme = Assertions.assertDoesNotThrow(() -> memeService.uploadMeme(userId, username, "random Title", file));
        Assertions.assertEquals("random Title", uploadedMeme.getTitle());
        Assertions.assertEquals(new Binary(file.getBytes()), uploadedMeme.getImage());
    }
}
