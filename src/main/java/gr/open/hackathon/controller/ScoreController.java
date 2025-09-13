package gr.open.hackathon.controller;

import gr.open.hackathon.service.ScoreService;
import gr.open.hackathon.service.TeamScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @PostMapping("/upload")
    public ResponseEntity<List<TeamScore>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            List<TeamScore> scores = scoreService.calculateScores(file);
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
