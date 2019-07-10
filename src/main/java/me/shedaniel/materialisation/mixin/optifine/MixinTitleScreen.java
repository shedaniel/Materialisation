package me.shedaniel.materialisation.mixin.optifine;

import com.google.common.collect.Lists;
import me.shedaniel.materialisation.config.ConfigHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    
    @Unique public boolean showed = false;
    
    @Inject(method = {"init"}, at = {@At("RETURN")})
    private void init(CallbackInfo info) {
        if (FabricLoader.getInstance().isModLoaded("optifabric") && !showed)
            if (!ConfigHelper.OPTIFINE_IGNORE_FILE.exists()) {
                Screen screen = (Screen) (Object) this;
                MinecraftClient.getInstance().openScreen(new ConfirmScreen(t -> {
                    if (t) {
                        MinecraftClient.getInstance().scheduleStop();
                    } else {
                        try {
                            ConfigHelper.OPTIFINE_IGNORE_FILE.createNewFile();
                            MinecraftClient.getInstance().openScreen(screen);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MinecraftClient.getInstance().openScreen(getCannotSaveIgnoreFile(e));
                        }
                    }
                }, new LiteralText("Materialisation does NOT support Optifine fully!").formatted(Formatting.RED), new LiteralText("There will be issues with rendering tool durability! Please remove Optifine if you want that feature. Otherwise, ignore this warning and play without the tool durability. More details are in the GitHub issues!"), "Close Minecraft", "Ignore Warning"));
                showed = true;
            }
    }
    
    @Unique
    private Screen getCannotSaveIgnoreFile(Exception e) {
        List<String> error = Lists.newArrayList();
        error.add("§lFailed to save config");
        error.add(" ");
        error.add(e.toString());
        StackTraceElement[] stackTrace = e.getStackTrace();
        for(int i = 0; i < stackTrace.length && i < 7; i++) {
            error.add("  at " + stackTrace[i]);
            if (i == 6) {
                error.add("Skipping " + (stackTrace.length - i) + " lines!");
            }
        }
        List<String> list = Lists.newArrayList();
        return new Screen(new LiteralText("")) {
            public void render(int int_1, int int_2, float float_1) {
                this.renderDirtBackground(0);
                float var10000 = (float) (this.height / 2);
                int y = Math.max((int) (var10000 - 9.0F * 1.3F / 2.0F * (float) list.size()), 30);
                for(int i = 0; i < list.size(); ++i) {
                    String s = (String) list.get(i);
                    this.drawString(this.minecraft.textRenderer, s, 20, y, -1);
                    y += 9;
                }
                super.render(int_1, int_2, float_1);
            }
            
            protected void init() {
                super.init();
                list.clear();
                error.forEach(s -> font.wrapStringToWidthAsList(s, this.width - 40).forEach(list::add));
                this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 26, 200, 20, "Exit Minecraft", (buttonWidget) -> {
                    MinecraftClient.getInstance().scheduleStop();
                }));
            }
            
            @Override
            public boolean shouldCloseOnEsc() {
                return false;
            }
        };
    }
    
}
