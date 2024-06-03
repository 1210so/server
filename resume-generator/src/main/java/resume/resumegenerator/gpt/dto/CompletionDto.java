package resume.resumegenerator.gpt.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * ChatGpt 프롬프트 요청 DTO
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletionDto {

    private String model;
    private List<Map<String, String>> messages;
    private float temperature = 0.7F;
    private int max_tokens = 1000;

    @Builder
    public CompletionDto(String model, List<Map<String, String>> messages,
                         float temperature, int max_tokens) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getMaxTokens() {
        return max_tokens;
    }
}


