package resume.resumegenerator.gpt.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import resume.resumegenerator.gpt.config.ChatGptConfig;
import resume.resumegenerator.gpt.dto.CompletionDto;
import resume.resumegenerator.gpt.service.ChatGptService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatGptServiceImpl3 implements ChatGptService {

    private final ChatGptConfig chatGptConfig;

    public ChatGptServiceImpl3(ChatGptConfig chatGptConfig) {
        this.chatGptConfig = chatGptConfig;
    }

    @Value("${openai.url.prompt}")
    private String legacyPromptUrl;

    /**
     * ChatGpt 모델에 대한 프롬프트
     */
    @Override
    public Map<String, Object> prompt(CompletionDto completionDto) {
        log.info("[+] ChatGpt 모델의 프롬프트를 수행합니다.");

        // OpenAI API 호출을 위한 요청 데이터 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", completionDto.getModel());
        requestBody.put("messages", completionDto.getMessages());
        requestBody.put("temperature", completionDto.getTemperature());
        requestBody.put("max_tokens", completionDto.getMaxTokens());

        // 토큰 정보가 포함된 Header를 가져온다.
        HttpHeaders headers = chatGptConfig.httpHeaders();

        // 통신을 위한 RestTemplate을 구성한다.
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = chatGptConfig
                .restTemplate()
                .exchange(legacyPromptUrl, HttpMethod.POST, requestEntity, String.class);

        // 응답 파싱해서 답변 추출
        Map<String, Object> resultMap = new HashMap<>();
        try {
            ObjectMapper om = new ObjectMapper();
            // String -> HashMap 역직렬화 구성
            resultMap = om.readValue(response.getBody().trim(), new TypeReference<>() {});
            // 모든 선택지의 텍스트를 결합하여 전체 텍스트로 구성
            List<Map<String, Object>> choices = (List<Map<String, Object>>) resultMap.get("choices");
            StringBuilder completeText = new StringBuilder();
            for (Map<String, Object> choice : choices) {
                Map<String, String> message = (Map<String, String>) choice.get("message");
                completeText.append(message.get("content").toString().trim());
            }
            resultMap.put("complete_text", completeText.toString());
        } catch (JsonMappingException e) {
            log.debug("JsonMappingException :: " + e.getMessage());
        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException :: " + e.getMessage());
        } catch (RuntimeException e) {
            log.debug("RuntimeException :: " + e.getMessage());
        }

        return resultMap;
    }
}




