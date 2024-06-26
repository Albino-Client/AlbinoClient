package ir.albino.client.features.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.slangware.ultralight.HtmlScreen;
import dev.slangware.ultralight.UltraManager;
import dev.slangware.ultralight.annotations.HTMLRoute;
import ir.albino.client.AlbinoClient;
import ir.albino.client.features.discord.DiscordState;
import ir.albino.client.features.ui.altmanager.AltManagerMenu;
import ir.albino.client.features.ui.chat.ChatMenu;
import ir.albino.client.http.ApiEvent;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;
import spark.Request;
import spark.Response;

import java.awt.*;
import java.io.IOException;

public class MainMenu extends HtmlScreen implements DiscordState {

    private final static AlbinoClient client = AlbinoClient.instance;

    public MainMenu() {
        super(UltraManager.getInstance().getViewController(), "mainmenu.html");

    }

    @HTMLRoute(path = "/api", method = "POST")
    public void onFunctionReceive(Request req, Response res) {
        ObjectMapper mapper = new ObjectMapper();
        ApiEvent event;
        try {
            event = mapper.readValue(req.body(), ApiEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (event.function != null && event.function.equals("on_click")) {
            if (event.id != null) {
                this.actionPerformed(event.id);
            }
        }
    }


    @Override
    protected void actionPerformed(int id) {
        switch (id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
                this.mc.displayGuiScreen(new AltManagerMenu(this));
                break;
            case 4:
                this.mc.shutdown();
                break;
            case 6:
                this.mc.displayGuiScreen(new ChatMenu(this));
                break;
            case 5:
                this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        final ScaledResolution sr = new ScaledResolution(mc);

        mc.getTextureManager().bindTexture(new ResourceLocation("Albino/images/background.png"));
        Gui.drawModalRectWithCustomSizedTexture(
                -21 + mouseX / 90,
                mouseY * -1 / 90,
                0.0f, 0.0f,
                this.width + 20,
                this.height + 20,
                (float) (this.width + 21),
                (float) (this.height + 20)
        );

        //RenderUtils.rect(5,10,50,100, new Color(0,0,0,155).getRGB());
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        AlbinoClient.instance.fontRenderer.getComfortaa().drawStringWithShadow(String.format(
                        "%s %s By AlbinoTeam",
                        AlbinoClient.instance.NAME,
                        AlbinoClient.instance.VERSION),
                5,
                sr.getScaledHeight() - 15,
                new Color(141, 211, 211).getRGB()
        );
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
