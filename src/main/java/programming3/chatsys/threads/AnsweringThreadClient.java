package programming3.chatsys.threads;

import programming3.chatsys.data.ChatMessage;

public class AnsweringThreadClient extends ThreadClient {

    AnsweringThreadClient(ThreadServer server, String name) {
        super(server, name);
    }

    @Override
    public void handleMessage(ChatMessage message) {
        super.handleMessage(message);

        if (message.getMessage() == "Hello World!") {
            this.send(new ChatMessage("Hello "+message.getUserName()+"!"));
        }
    }
}
