package me.shedaniel.materialisation.modmenu;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MaterialisationCloth {
    
    public static ConfigScreenFactory<?> config = MaterialisationMaterialsScreen::new;
    
    public static Text color(Text text, Formatting formatting) {
        return text.visit(new Text.StyledVisitor<Text>() {
            TextCollector collector = new TextCollector();
            
            @Override
            public Optional<Text> accept(Style style, String asString) {
                collector.add(StringVisitable.styled(asString, style));
                return Optional.of((Text) collector.getCombined());
            }
        }, Style.EMPTY.withFormatting(formatting)).orElse(text);
    }
    
    public static OrderedText color(OrderedText text, Formatting formatting) {
        return visitor -> {
            return text.accept((index, style, codePoint) -> visitor.accept(index, style.withFormatting(formatting), codePoint));
        };
    }
    
    public static Text wrap(StringVisitable text) {
        MutableText result = new LiteralText("");
        text.visit(new Text.StyledVisitor<Text>() {
            MutableText text = new LiteralText("");
            
            @Override
            public Optional<Text> accept(Style style, String asString) {
                result.append(new LiteralText(asString).fillStyle(style));
                return Optional.empty();
            }
        }, Style.EMPTY).orElse(Text.of(text.getString()));
        return result;
    }
    
}
