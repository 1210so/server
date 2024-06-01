package resume.resumegenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resume.resumegenerator.domain.entity.IntroductionInfo;
import resume.resumegenerator.gpt.dto.CompletionDto;
import resume.resumegenerator.gpt.service.ChatGptService;
import resume.resumegenerator.store.IntroductionInfoStore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/introduction-info")
public class IntroductionInfoController {

    private final IntroductionInfoStore introductionInfoStore;
    private final ChatGptService chatGptService;

    @Autowired
    public IntroductionInfoController(IntroductionInfoStore introductionInfoStore, ChatGptService chatGptService) {
        this.introductionInfoStore = introductionInfoStore;
        this.chatGptService = chatGptService;
    }

    @PostMapping("/save/{userId}")
    public ResponseEntity<IntroductionInfo> saveAndGenerateIntroductionInfo(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // GPT 프롬프트 생성
        CompletionDto completionDto = CompletionDto.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(prompt)
                .temperature(0.7f)
                .max_tokens(150)
                .build();

        Map<String, Object> gptResponse = chatGptService.prompt(completionDto);
        String generatedText = null;

        if (gptResponse.containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) gptResponse.get("choices");
            if (!choices.isEmpty() && choices.get(0).containsKey("text")) {
                generatedText = (String) choices.get(0).get("text");
            }
        }

        if (generatedText == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // 생성된 텍스트 저장
        IntroductionInfo introductionInfo = new IntroductionInfo();
        introductionInfo.setUserId(userId);
        introductionInfo.setGpt(generatedText.trim());

        IntroductionInfo savedInfo = introductionInfoStore.save(userId, introductionInfo);
        if (savedInfo != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInfo);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<IntroductionInfo> updateIntroductionInfo(@PathVariable Long userId, @RequestBody IntroductionInfo introductionInfo) {
        if (!introductionInfoStore.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }

        introductionInfo.setUserId(userId);
        IntroductionInfo updatedInfo = introductionInfoStore.update(userId, introductionInfo);
        return ResponseEntity.ok(updatedInfo);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<IntroductionInfo> getIntroductionInfo(@PathVariable Long userId) {
        IntroductionInfo introductionInfo = introductionInfoStore.findById(userId);
        if (introductionInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(introductionInfo);
    }
}



