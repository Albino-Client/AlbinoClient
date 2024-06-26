package ir.albino.client;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import dev.slangware.ultralight.UltraManager;
import ir.albino.client.event.EventManager;
import ir.albino.client.features.account.AltManager;
import ir.albino.client.features.modules.Module;
import ir.albino.client.features.modules.ModuleManager;
import ir.albino.client.utils.Common;
import ir.albino.client.utils.render.font.FontManager;
import lombok.Getter;
import net.janrupf.ujr.api.javascript.JSClass;
import net.janrupf.ujr.api.javascript.JSGlobalContext;
import net.minecraft.client.audio.SoundList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbinoClient {
    @Getter
    public String NAME = "Albino";
    @Getter
    public String VERSION = "1.0";
    @Getter
    private final Logger logger = LogManager.getLogger(AlbinoClient.class);

    public static AlbinoClient instance = new AlbinoClient();
    public FontManager fontRenderer;
    public AltManager altManager;
    public SoundList soundList;
    public EventManager eventManager;
    public ModuleManager moduleManager;
    public ConcurrentLinkedQueue<Module> modules = new ConcurrentLinkedQueue<>();
    public boolean debug = false;
    public JSGlobalContext context;
    public ExecutorService executorService;
    public IPCClient client;
    public RichPresence.Builder richPresence;

    public void start() {
        client = new IPCClient(1221159378724589629L);
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                richPresence = new RichPresence.Builder()
                        .setState("Idling").setDetails("AlbinoClient 1.8 | Iranian Client")
                        .setStartTimestamp(OffsetDateTime.now())
                        .setLargeImage("game_icon", "AlbinoLarge");
                client.sendRichPresence(richPresence.build());
            }
        });
        try {
            client.connect();
        } catch (NoDiscordClientException e) {
            throw new RuntimeException(e);
        }
        this.executorService = Executors.newSingleThreadExecutor();
        this.detectAccounts();
        this.eventManager = new EventManager();
        this.fontRenderer = new FontManager();
        this.moduleManager = new ModuleManager();
        moduleManager.initModules();
        Display.setTitle(this.NAME);
        this.soundList = new SoundList();
        UltraManager.getInstance().init("/templates");
        context = new JSGlobalContext((JSClass) null);
//        try {
//            new Radio("", "est").init();
//        } catch (IOException | JavaLayerException e) {
        //            throw new RuntimeException(e);
        //        }

    }

    public void shutDown() {

    }


    public void detectAccounts() {
        File users = new File(Common.getGamePath(), "users.json");
        if (users.exists()) {
            try {
                altManager = new JsonMapper().readValue(users, AltManager.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
