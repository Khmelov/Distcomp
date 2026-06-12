package by.bsuir;

import by.bsuir.discussion.DiscussionApplication;
import by.bsuir.distcomp.Task320Application;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class Task330Launcher {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DiscussionApplication.class)
                .run(withServerArgs(args, "24130"));

        new SpringApplicationBuilder(Task320Application.class)
                .run(withServerArgs(args, "24110"));
    }

    private static String[] withServerArgs(String[] args, String port) {
        String[] serverArgs = {
                "--server.port=" + port,
                "--spring.jmx.enabled=false"
        };
        String[] result = new String[args.length + serverArgs.length];
        System.arraycopy(args, 0, result, 0, args.length);
        System.arraycopy(serverArgs, 0, result, args.length, serverArgs.length);
        return result;
    }
}
