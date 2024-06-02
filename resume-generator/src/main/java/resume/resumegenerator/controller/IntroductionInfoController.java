package resume.resumegenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resume.resumegenerator.domain.entity.IntroductionInfo;
import resume.resumegenerator.domain.entity.PersonalInfo;
import resume.resumegenerator.gpt.dto.CompletionDto;
import resume.resumegenerator.gpt.service.ChatGptService;
import resume.resumegenerator.store.IntroductionInfoStore;
import resume.resumegenerator.store.PersonalInfoStore;

import java.util.Map;

@RestController
@RequestMapping("/introduction-info")
public class IntroductionInfoController {

    private final IntroductionInfoStore introductionInfoStore;
    private final ChatGptService chatGptService;
    private final PersonalInfoStore personalInfoStore;

    @Autowired
    public IntroductionInfoController(IntroductionInfoStore introductionInfoStore, ChatGptService chatGptService, PersonalInfoStore personalInfoStore) {
        this.introductionInfoStore = introductionInfoStore;
        this.chatGptService = chatGptService;
        this.personalInfoStore = personalInfoStore;
    }

    @PostMapping("/save/{userId}")
    public ResponseEntity<IntroductionInfo> saveAndGenerateIntroductionInfo(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // 사용자 정보 가져오기
        PersonalInfo personalInfo = personalInfoStore.findById(userId);
        if (personalInfo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String birth = personalInfo.getBirth();

        // GPT 프롬프트 생성
        CompletionDto completionDto = CompletionDto.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(prompt)
                .temperature(0.7f)
                .max_tokens(800)
                .build();

        // 생년월일을 포함한 프롬프트 구성
        String fullPrompt = String.format("%s\n\n사용자의 생년월일: %s", completionDto.getPrompt(), birth);
        completionDto.setPrompt(fullPrompt);

        Map<String, Object> gptResponse = chatGptService.prompt(completionDto);
        String generatedText = null;

        if (gptResponse.containsKey("complete_text")) {
            generatedText = (String) gptResponse.get("complete_text");
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

