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

        String input = completionDto.getPrompt();

        // 선택된 항목들을 포맷에 맞춰 구성
        StringBuilder formattedInput = new StringBuilder();
        formattedInput.append("당신은 해당 생년월일의 구직자입니다. 아래의 제약조건을 지켜서 출력형식에 따라 300자 이내의 이력서 자기소개서를 한글로 작성해주세요.\n");
        formattedInput.append("#제약 조건 : \n- 선택한 항목의 핵심 표현을 사용해야 한다.\n- 문장은 공식적이고 명확하게 작성한다\n - 이름, 생년월일을 자기소개서에 작성하면 안된다.\n");
        formattedInput.append("#출력 형식 : 저는 항상 책임감을 가지고 살아온 사람입니다. 오랜 시간 동안 다양한 직무를 맡아 왔으며, 맡은 바 책임을 다해 완수해 왔습니다. 또한, 친화력이 뛰어나 여러 세대와 원활하게 소통하며, 다양한 사람들과의 협업을 통해 좋은 결과를 만들어냈습니다.");
        formattedInput.append("#선택한 항목: " + input + "\n");

        log.info("질문: {}", formattedInput.toString());

        completionDto.setModel("gpt-3.5-turbo-instruct"); // gpt 모델 이름
        completionDto.setMaxTokens(800); // 최대 토큰 설정
        completionDto.setPrompt(formattedInput.toString());

        // 토큰 정보가 포함된 Header를 가져온다.
        HttpHeaders headers = chatGptConfig.httpHeaders();

        // 통신을 위한 RestTemplate을 구성한다.
        HttpEntity<CompletionDto> requestEntity = new HttpEntity<>(completionDto, headers);
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
                completeText.append(choice.get("text").toString().trim());
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
