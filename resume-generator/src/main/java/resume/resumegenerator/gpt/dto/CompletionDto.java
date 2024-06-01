package resume.resumegenerator.gpt.dto;

import lombok.*;

/**
 * ChatGpt 프롬프트 요청 DTO
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletionDto {

    // 사용할 모델
    private String model;

    // 사용할 프롬프트 명령어
    private String prompt;

    // 프롬프트의 다양성을 조절할 명령어(default : 1)
    private float temperature = 0;

    // 최대 사용할 토큰 (default : 1000)
    private int max_tokens = 1000;

    @Builder
    public CompletionDto(String model, String prompt, float temperature, int max_tokens) {
        this.model = model;
        this.prompt = prompt;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }

    // 프롬프트 설정 메서드
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    // ChatGpt 모델 설정 메서드
    public void setModel(String model) {
        this.model = model;
    }

    // 최대 토큰 설정 메서드
    public void setMaxTokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }
}
