package org.etjen.spring_ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAIController {
    private OpenAiChatModel openAiChatModel;
    private ChatClient chatClient;

    public OpenAIController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.chatClient = ChatClient.create(openAiChatModel);
    }

    @GetMapping("api/{message}")
    public String getAnswer(@PathVariable String message){
        return openAiChatModel.call("Provide simple one sentence answer/reply/whatever: " + message);
    }

    @GetMapping("api/chat-client/{message}")
    public ResponseEntity<String> getAnswerFromChatClient(@PathVariable String message){
        String response = chatClient.prompt(message).call().content();
        return ResponseEntity.ok("With chat client: " + response);
    }

    @GetMapping("api/chat-response/{message}")
    public ResponseEntity<String> getAnswerFromChatResponse(@PathVariable String message){
        ChatResponse chatResponse = chatClient.prompt(message).call().chatResponse();
        System.out.println(chatResponse.getMetadata().getModel());
        String response = chatResponse.getResult().getOutput().getText();
        return ResponseEntity.ok("With chat client: " + response);
    }
}
