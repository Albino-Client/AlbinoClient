import ir.albino.client.AlbinoClient;
import net.minecraft.client.main.Main;

import java.util.Arrays;

public class Start {
    /**
     * Entry point of the client
     *
     * @param args
     */
    public static void main(String[] args) {
        AlbinoClient.instance.getLogger().info(String.format("Loading %s %s",
                AlbinoClient.instance.getNAME(),
                AlbinoClient.instance.getVERSION()));


        final String[] input = new String[]{
                "--version", "albinoclient",
                "--accessToken", "0",
                "--assetsDir", "assets",
                "--assetIndex", "1.8",
                "--userProperties", "{}"
        };

        final String[] result = Arrays.copyOf(input, input.length + args.length);
        System.arraycopy(args, 0, result, input.length, args.length);

        Main.main(result, System.currentTimeMillis());
    }
}
