package resume.resumegenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resume.resumegenerator.domain.entity.CareerInfo;
import resume.resumegenerator.domain.entity.IntroductionInfo;
import resume.resumegenerator.domain.entity.PersonalInfo;
import resume.resumegenerator.gpt.dto.CompletionDto;
import resume.resumegenerator.gpt.service.ChatGptService;
import resume.resumegenerator.store.CareerInfoStore;
import resume.resumegenerator.store.IntroductionInfoStore;
import resume.resumegenerator.store.PersonalInfoStore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/introduction-info")
public class IntroductionInfoController {

    private final IntroductionInfoStore introductionInfoStore;
    private final ChatGptService chatGptService;
    private final PersonalInfoStore personalInfoStore;
    private final CareerInfoStore careerInfoStore;

    @Autowired
    public IntroductionInfoController(IntroductionInfoStore introductionInfoStore, ChatGptService chatGptService,
                                      PersonalInfoStore personalInfoStore, CareerInfoStore careerInfoStore) {
        this.introductionInfoStore = introductionInfoStore;
        this.chatGptService = chatGptService;
        this.personalInfoStore = personalInfoStore;
        this.careerInfoStore = careerInfoStore;
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

        // 사용자 경력 정보 가져오기
        List<CareerInfo> careerInfos = careerInfoStore.findById(userId);
        String careerDetails = careerInfos.stream()
                .map(career -> String.format("근무처: %s, 근무 기간: %s, 업무 내용: %s", career.getPlace(), career.getPeriod(), career.getTask()))
                .collect(Collectors.joining("\n"));

// 생년월일과 경력을 포함한 프롬프트 구성
        StringBuilder fullPrompt = new StringBuilder();
        fullPrompt.append("당신은 아래의 3가지 특성을 가진 구직자입니다. 아래의 제약조건을 지켜서 이력서에 사용할 자기소개서를 한글로 작성해주세요.\n")
                .append("#제약 조건 : \n")
                .append("- 공백 포함 300자 이내여야 한다.\n")
                .append("- 문장은 공식적이고 명확하게 작성한다.\n")
                .append("- 해당 생년월일을 가진 구직자라는 걸 명심하고, 이름과 생년월일을 자기소개서에 작성하면 안된다.\n")
                .append("- 해당 특성이 어떻게 업무에 기여했는지, 어떤 상황에서 발휘되었는지 설명해야 한다.\n")
                .append("- 너무 억지스러우면 안되고, 경력이 없다면 특성을 잘 보여주도록 작성해야 한다.\n")
                .append("- 특정 회사나 직무를 언급하지 않고 일반적인 구직 상황을 가정하여 작성해야 한다.\n")
                .append("#3가지 특성: ").append(prompt).append("\n")
                .append("사용자의 생년월일: ").append(birth).append("\n")
                .append("경력:\n").append(careerDetails).append("\n\n");

        // GPT 프롬프트 생성
        CompletionDto completionDto = CompletionDto.builder()
                .model("gpt-4")
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
