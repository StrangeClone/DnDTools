package org.example.call;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will send a request to ChatGPT to generate a text, given a prompt
 */
public class Call {

    static final String KEY = "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";
    private boolean ready = false;
    private final String result;

    /**
     * Creates a request to ChatGPT, using the string passed as parameter as the prompt;
     * IT SHOULD BE USED IN A SEPARATE THREAD, as it will block, waiting for the server response;
     *
     * @param prompt the prompt to pass to the AI
     */
    public Call(String prompt) {
        OpenAiService service = new OpenAiService(KEY);
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt);
        messages.add(systemMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .build();

        StringBuilder stringBuilder = new StringBuilder();
        service.streamChatCompletion(chatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(s -> {
                    String content = s.getChoices().get(0).getMessage().getContent();
                    if (content != null) {
                        stringBuilder.append(content);
                    }
                });

        result = stringBuilder.toString();
        ready = true;
        service.shutdownExecutor();
    }

    public boolean notReady() {
        return !ready;
    }

    public String getResult() {
        if (notReady()) {
            throw new IllegalStateException("The result isn't ready.");
        }
        return result;
    }
}
