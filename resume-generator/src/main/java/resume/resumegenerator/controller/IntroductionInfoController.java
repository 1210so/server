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

import java.util.List;
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

        // 프롬프트를 세 가지 표현으로 분리
        String[] personalityTraits = prompt.split(",");
        if (personalityTraits.length != 3) {
            return ResponseEntity.badRequest().body(null);
        }

        // 사용자 정보 가져오기
        PersonalInfo personalInfo = personalInfoStore.findById(userId);
        if (personalInfo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String birth = personalInfo.getBirth();

        //생년월일을 포함한 프롬프트 구성
        StringBuilder fullPrompt = new StringBuilder();
        fullPrompt.append("당신은 해당 생년월일의 구직자입니다. 아래의 제약조건을 지켜서 출력형식에 따라 300자 이내의 이력서 자기소개서를 한글로 작성해주세요.\n")
                .append("#제약 조건 : \n- 선택한 항목의 핵심 표현을 사용해야 한다.\n- 문장은 공식적이고 명확하게 작성한다\n- 이름, 생년월일을 자기소개서에 작성하면 안된다.\n")
                .append("#출력 형식 : 저는 항상 책임감을 가지고 살아온 사람입니다. 오랜 시간 동안 다양한 직무를 맡아 왔으며, 맡은 바 책임을 다해 완수해 왔습니다. 또한, 친화력이 뛰어나 여러 세대와 원활하게 소통하며, 다양한 사람들과의 협업을 통해 좋은 결과를 만들어냈습니다.\n")
                .append("#선택한 항목: ").append(prompt).append("\n")
                .append("사용자의 생년월일: ").append(birth);

        // GPT 프롬프트 생성
        CompletionDto completionDto = CompletionDto.builder()
                .model("gpt-4o")
                .messages(List.of(Map.of("role", "user", "content", fullPrompt.toString())))
                .temperature(0.7f)
                .max_tokens(1000)
                .build();

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
        introductionInfo.setPersonality1(personalityTraits[0].trim());
        introductionInfo.setPersonality2(personalityTraits[1].trim());
        introductionInfo.setPersonality3(personalityTraits[2].trim());

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


