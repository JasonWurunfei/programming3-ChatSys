package programming3.chatsys.threads;

import programming3.chatsys.data.Database;
import programming3.chatsys.data.TextDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunChat {
    public static void main(String[] args) {
        Database db = new TextDatabase();
        ExecutorService exec = Executors.newCachedThreadPool();

        ThreadServer server = new ThreadServer(db);
        exec.submit(server);

        ThreadClient client1 = new ThreadClient(server, "client1");
        exec.submit(client1);

        ThreadClient client2 = new ThreadClient(server, "client2");
        exec.submit(client2);

        exec.shutdown();
        try {
            if(!exec.awaitTermination(3, TimeUnit.SECONDS))
                exec.shutdownNow();
        } catch (InterruptedException ignore) {
            exec.shutdownNow();
        }
    }
}
