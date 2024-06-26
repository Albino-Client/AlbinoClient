package ir.albino.client.features.modules;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.albino.client.AlbinoClient;
import ir.albino.client.features.modules.configuration.ModuleTheme;
import ir.albino.client.features.modules.settings.ModuleSetting;
import lombok.Data;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;


@Data
public class Module {
    @JsonIgnore
    protected int x;
    @JsonIgnore
    protected int y;
    @JsonIgnore
    protected int width;
    @JsonIgnore
    protected int height;
    @JsonIgnore
    private String name;
    @JsonIgnore
    private String description;
    @JsonIgnore
    private String version;
    @JsonIgnore
    private ModuleInfo.Category category;
    @JsonIgnore
    private boolean draggable;
    @JsonIgnore
    private boolean dragging;
    public List<ModuleSetting<?>> settings = new ArrayList<>();
    private boolean enabled;
    @JsonIgnore
    public final Minecraft mc = Minecraft.getMinecraft();
    @JsonIgnore
    public ModuleTheme theme = AlbinoClient.instance.moduleManager.getModuleTheme();

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onInit() {
    }

    public final void toggle() {
        this.enabled = !enabled;
        AlbinoClient client = AlbinoClient.instance;
        if (enabled) {
            onEnable();
            client.eventManager.register(this);
        } else {
            onDisable();
            client.eventManager.unregister(this);
        }
    }

    @Override
    public String toString() {
        return String.format("%s-%s", getName(), getVersion());
    }
}

