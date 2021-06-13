package tk.avicia.avomod.events;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import tk.avicia.avomod.Avomod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    public static void execute(ClientChatReceivedEvent event) {
        doChecks(event.getMessage());
        for (ITextComponent textComponent : event.getMessage().getSiblings()) {
            if (textComponent.getSiblings().size() > 0) {
                for (ITextComponent textComponent1 : textComponent.getSiblings()) {
                    doChecks(textComponent1);
                }
            } else {
                doChecks(textComponent);
            }
        }

    }

    private static void doChecks(ITextComponent textComponent) {
        if (checkIfNickHover(textComponent) && !checkIfCompassHover(textComponent)) {
            addRealNameToTextComponent(textComponent);
        }
        if (checkIfDM(textComponent)) {
            makeDMClickWork(textComponent);
        }
    }

    private static boolean checkIfNickHover(ITextComponent textComponent) {
        HoverEvent hover = textComponent.getStyle().getHoverEvent();

        if (hover != null) {
            String hoverText = hover.getValue().getUnformattedText();
            return hoverText.contains("real username");
        }
        return false;
    }

    // Wynntils adds a compass hover when it finds what it thinks is coordinates, which a nickname can be
    // the nickname becomes double since the message gets sens twice, once by wynntils and once by wynncraft
    private static boolean checkIfCompassHover(ITextComponent textComponent) {
        HoverEvent hover = textComponent.getStyle().getHoverEvent();

        if (hover != null) {
            String hoverText = hover.getValue().getUnformattedText();
            return hoverText.contains("/compass");
        }
        return false;
    }

    private static void addRealNameToTextComponent(ITextComponent textComponent) {
        if (checkIfNickHover(textComponent)) {
            HoverEvent hover = textComponent.getStyle().getHoverEvent();
            String hoverText = hover.getValue().getUnformattedText();
            String realName = hoverText.split(" ")[hoverText.split(" ").length - 1];

            ITextComponent realNameTextComponent = new TextComponentString("(" + realName + ")");
            Style componentStyle = new Style();
            componentStyle.setColor(TextFormatting.RED);
            realNameTextComponent.setStyle(componentStyle);

            textComponent.appendSibling(realNameTextComponent);
        }
    }

    private static boolean checkIfDM(ITextComponent textComponent) {
        Pattern pattern = Pattern.compile("^\\[" + Avomod.getMC().player.getDisplayNameString() + " \u27a4 .*] .*", Pattern.CASE_INSENSITIVE);
        System.out.println("[" + Avomod.getMC().player.getDisplayNameString() + " ➤ _Beanb] Hello");
        String messageString = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
        System.out.println(messageString);
        System.out.println(pattern.matcher(messageString).find());
        return pattern.matcher(messageString).find();
    }

    private static void makeDMClickWork(ITextComponent textComponent) {
        if (checkIfDM(textComponent)) {
            String messageString = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
            String command = "/msg" + messageString.substring(messageString.indexOf("\u27a4") + 1, messageString.indexOf("]")).replaceAll(" \\(.*\\)", "") + " ";
            // Needs to be split into a bunch of tiny pieces
            // Can probably modify the textcomponenents (siblings) directly
            for (ITextComponent sibling : textComponent.getSiblings()) {
                sibling.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
            }
//            ITextComponent clickableText = new TextComponentString(messageString.split("] ")[0] + "]");
//            clickableText.setStyle(textComponent.getStyle().createShallowCopy());
//            clickableText.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
//            textComponent.getSiblings().clear();
//            textComponent.appendSibling(clickableText);
//            textComponent.appendSibling(new TextComponentString(messageString.split("] ")[1] + " "));
        }
    }
}
