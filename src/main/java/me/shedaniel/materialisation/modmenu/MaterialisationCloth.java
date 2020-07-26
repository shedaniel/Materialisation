package me.shedaniel.materialisation.modmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class MaterialisationCloth {
    
    @SuppressWarnings("Convert2MethodRef") public static Function<Screen, Screen> config = screen -> new MaterialisationMaterialsScreen(screen);
    
    public static StringRenderable color(StringRenderable text, Formatting formatting) {
        return text.visit(new StringRenderable.StyledVisitor<StringRenderable>() {
            TextCollector collector = new TextCollector();
            
            @Override
            public Optional<StringRenderable> accept(Style style, String asString) {
                collector.add(StringRenderable.styled(asString, style));
                return Optional.of(collector.getCombined());
            }
        }, Style.EMPTY.withFormatting(formatting)).orElse(text);
    }
    
    public static Text wrap(StringRenderable text) {
        MutableText result = new LiteralText("");
        text.visit(new StringRenderable.StyledVisitor<Text>() {
            MutableText text = new LiteralText("");
            
            @Override
            public Optional<Text> accept(Style style, String asString) {
                result.append(new LiteralText(asString).fillStyle(style));
                return Optional.empty();
            }
        }, Style.EMPTY).orElse(Text.method_30163(text.getString()));
        return result;
    }
    
}
