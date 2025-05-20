package org.etjen.spring_ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController()
public class OpenAIController {
    private final OpenAiChatModel openAiChatModel;
    private final ChatClient chatClient;

//    public OpenAIController(OpenAiChatModel openAiChatModel) {
//        this.openAiChatModel = openAiChatModel;
//        this.chatClient = ChatClient.create(openAiChatModel);
//    }

    public OpenAIController(OpenAiChatModel openAiChatModel, ChatClient.Builder builder) {
        this.openAiChatModel = openAiChatModel;
        this.chatClient = builder.build();
    }

    @GetMapping("{message}")
    public String getAnswer(@PathVariable String message){
        return openAiChatModel.call("Provide simple one sentence answer/reply/whatever: " + message);
    }

    @GetMapping("chat-client/{message}")
    public ResponseEntity<String> getAnswerFromChatClient(@PathVariable String message){
        String response = chatClient.prompt(message).call().content();
        return ResponseEntity.ok("With chat client: " + response);
    }

    @GetMapping("chat-response/{message}")
    public ResponseEntity<String> getAnswerFromChatResponse(@PathVariable String message){
        ChatResponse chatResponse = chatClient.prompt(message).call().chatResponse();
        System.out.println(chatResponse.getMetadata().getModel());
        String response = chatResponse.getResult().getOutput().getText();
        return ResponseEntity.ok("With chat client: " + response);
    }

    @PostMapping("chat-template/movie-recommend")
    public String recommendMovie(@RequestBody RecommendMoviePromptClass recommendMoviePromptClass){
        String template = """
                        I want to watch a {type} movie tonight with good rating,
                        looking for movies around this year {year}.
                        The language I'm looking for is {lang}.
                        Suggest one specific movie and tell me the cast and length of the movie.
                        
                        Response format should be exactly like this:
                        1. Movie name
                        2. Basic plot
                        3. Cast
                        4. Length
                        5. IMDB rating
                        """;
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("type", recommendMoviePromptClass.getType(), "year", recommendMoviePromptClass.getYear(), "lang", recommendMoviePromptClass.getLang()));

        String response = chatClient.prompt(prompt).call().content();
        return response;
    }
}
