package tk.avicia.avomod.events;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class RealName {
    public static void execute(ClientChatReceivedEvent event) {
        for (ITextComponent textComponent : event.getMessage().getSiblings()) {
            if (textComponent.getSiblings().size() > 0) {
                for (ITextComponent textComponent1 : textComponent.getSiblings()) {
                    checkIfNickHover(textComponent1);
                }
            }

            checkIfNickHover(textComponent);
        }
    }

    private static void checkIfNickHover(ITextComponent textComponent) {
        HoverEvent hover = textComponent.getStyle().getHoverEvent();

        if (hover != null) {
            String hoverText = hover.getValue().getUnformattedText();

            if (hoverText.contains("real username")) {
                String realName = hoverText.split(" ")[hoverText.split(" ").length - 1];

                ITextComponent realNameTextComponent = new TextComponentString(":" + realName);
                Style componentStyle = new Style();
                componentStyle.setColor(TextFormatting.RED);
                realNameTextComponent.setStyle(componentStyle);

                textComponent.appendSibling(realNameTextComponent);
            }
        }
    }
}
