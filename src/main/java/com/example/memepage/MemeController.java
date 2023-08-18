package com.example.memepage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class MemeController {

    private final MemeService memeService;

    Logger logger = LoggerFactory.getLogger(getClass());

    public MemeController(MemeService memeService) {
        this.memeService = memeService;
    }

    @GetMapping("/meme/{id}")
    public String getMeme(Model model, @PathVariable("id") String id) {
        Optional<Meme> meme = memeService.getMeme(id);
        meme.ifPresent(value -> model.addAttribute("meme", value));
        return "memepage/singleMemeView";
    }

    @GetMapping
    public String home(Model model) {
        List<Meme> allMemes = memeService.getAllMemes();
        model.addAttribute("memeList", allMemes);
        return "memepage/index";
    }

    @GetMapping("/top")
    public String newMemes(Model model) {
        List<Meme> newMemes = memeService.getTopMemes();
        model.addAttribute("memeList", newMemes);
        return "memepage/index";
    }

    @GetMapping("/newpost")
    public String createPost() {
        return "memepage/newMemePost";
    }

    @GetMapping("/myPosts")
    public String createPost(Authentication authentication, Model model) {
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        String userid = principal.getSubject();
        List<Meme> memesOfUser = memeService.getMemesByUser(userid);
        model.addAttribute("memeList", memesOfUser);
        return "memepage/index";
    }

    @PostMapping("/newpost")
    public String createPost(Authentication authentication, @RequestPart String title, @RequestPart MultipartFile image) throws IOException {
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        String userid = principal.getSubject();
        String username = principal.getName();
        memeService.uploadMeme(userid, username, title, image);
        return "memepage/index";
    }

    @PostMapping("/upvote/{id}")
    public String upVoteMeme(Authentication authentication, @PathVariable("id") String id) {
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        String userId = principal.getSubject();
        memeService.upVoteMeme(id, userId);
        return "memepage/index";
    }

    @PostMapping("/downvote/{id}")
    public String downVoteMeme(Authentication authentication, @PathVariable("id") String id) {
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        String userId = principal.getSubject();
        memeService.downVoteMeme(id, userId);
        return "memepage/index";
    }

}
