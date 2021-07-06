package tk.avicia.avomod.events;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import tk.avicia.avomod.Avomod;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ChatUtils {
    private static ITextComponent fullMessage;
    private static String guildMessageSenderNickname = "";

    public static void execute(ClientChatReceivedEvent event) {
        fullMessage = event.getMessage();
        guildMessageSenderNickname = "";
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
        if (!guildMessageSenderNickname.equals("")) {
            try {
                // Save all sibling of the message
                List<ITextComponent> siblings = fullMessage.getSiblings();
                // Make a textcomponent with the real name
                fullMessage = new TextComponentString(TextFormatting.RED + "(" + guildMessageSenderNickname + ")");
                // Add all old siblings to the real name
                fullMessage.getSiblings().addAll(siblings);
                // Clears everything except for the nicknameand [***] stuff
                event.getMessage().getSiblings().clear();
                // Adds the real name + the original message after the nickname
                event.getMessage().appendSibling(fullMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void doChecks(ITextComponent textComponent) {
        if (checkIfGuildChat(textComponent)) {
            makeHereRunFindCommand(textComponent);
        }
        if (checkIfNickHover(textComponent)) {
            addRealNameToTextComponent(textComponent);
        }
        if (checkIfDM(textComponent)) {
            makeDMClickWork(textComponent);
        }
        if (checkIfShout(textComponent)) {
            makeShoutClickSuggestMsg(textComponent);
        }
    }

    private static boolean checkIfNickHover(ITextComponent textComponent) {
        HoverEvent hover = textComponent.getStyle().getHoverEvent();
        boolean res;
//        res = textComponent.getUnformattedText().contains("\u00A7o");
        res = textComponent.getSiblings().size() == 0;
        if (hover != null) {
            String hoverText = hover.getValue().getUnformattedText();
            res = res && hoverText.contains("real username");
        } else {
            res = false;
        }
        return res;
    }


    private static void addRealNameToTextComponent(ITextComponent textComponent) {
        if (checkIfNickHover(textComponent)) {
            HoverEvent hover = textComponent.getStyle().getHoverEvent();
            String hoverText = hover.getValue().getUnformattedText();
            String realName = hoverText.split(" ")[hoverText.split(" ").length - 1];

            // I never managed to get it to work with wynntils' coordinates but now it adds the ext directly
            // to the same thing as the nickname instead of as a sibling (not much difference) but it does make both
            // duplicates of the name reveal red when there are wynntils coordinates
            if (hoverText.contains("Rank:")) {
                // If it's guild chat there is both rank and real name in the hover and it breaks stuff
                // This needs to be done after the for loop to avoid errors, hence the variable
                guildMessageSenderNickname = realName;
            } else {
                textComponent.appendText(TextFormatting.RED + "(" + realName + ")" + textComponent.getStyle().getFormattingCode());
            }
        }
    }

    private static boolean checkIfDM(ITextComponent textComponent) {
        Pattern pattern = Pattern.compile("^\\[" + Avomod.getMC().player.getDisplayNameString() + " \u27a4 .*] .*", Pattern.CASE_INSENSITIVE);
        String messageString = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
        return pattern.matcher(messageString).find();
    }

    private static void makeDMClickWork(ITextComponent textComponent) {
        if (checkIfDM(textComponent)) {
            String messageString = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
            String command = "/msg" + messageString.substring(messageString.indexOf("\u27a4") + 1, messageString.indexOf("]")).replaceAll(" \\(.*\\)", "") + " ";

            for (ITextComponent sibling : textComponent.getSiblings()) {
                if (sibling.getStyle().getClickEvent() != null) {
                    // Doesn't override links in chat
                    if (!sibling.getStyle().getClickEvent().getValue().contains("http")
                            && !sibling.getStyle().getClickEvent().getValue().contains("://")) {
                        sibling.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
                    }
                } else {
                    sibling.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
                }
            }
        }
    }

    private static boolean checkIfShout(ITextComponent textComponent) {
        Pattern pattern = Pattern.compile("^(.* \\[WC\\d*] shouts:) .*", Pattern.CASE_INSENSITIVE);
        String messageString = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
        return pattern.matcher(messageString).find();
    }

    private static void makeShoutClickSuggestMsg(ITextComponent textComponent) {
        if (checkIfShout(textComponent)) {
            String messageString = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
            String command = "/msg " + messageString.substring(0, messageString.indexOf("[") - 1) + " ";

            for (ITextComponent sibling : textComponent.getSiblings()) {
                if (sibling.getStyle().getClickEvent() != null) {
                    // Doesn't override links in chat
                    if (!sibling.getStyle().getClickEvent().getValue().contains("http")
                            && !sibling.getStyle().getClickEvent().getValue().contains("://")) {
                        sibling.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
                    }
                } else {
                    sibling.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
                }
            }
        }
    }

    private static boolean checkIfGuildChat(ITextComponent textComponent) {
        if (!textComponent.getFormattedText().startsWith("\u00A73")) return false;
        if (textComponent.getSiblings().size() == 0) return false;
        Pattern pattern = Pattern.compile("^(\\[\u2605*[A-Za-z_]*]) .*", Pattern.CASE_INSENSITIVE);
        String messageString = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
        if (messageString.startsWith("[Info]")) return false;
        return pattern.matcher(messageString).find();
    }

    // Adds a clickevent to every "here" in guild chat that runs /find <Player> similar to wynntils' coordindates
    private static void makeHereRunFindCommand(ITextComponent textComponent) {
        if (checkIfGuildChat(textComponent)) {
            String fullMessage = TextFormatting.getTextWithoutFormattingCodes(textComponent.getUnformattedText());
            ITextComponent sibling = textComponent.getSiblings().get(textComponent.getSiblings().size() - 1);
            String siblingText = TextFormatting.getTextWithoutFormattingCodes(sibling.getUnformattedText());
            ITextComponent temp = new TextComponentString("");
            // If there is a standalone "here" in the string
            if (Arrays.stream(siblingText.split(" ")).anyMatch("here"::equals)) {
                String splitString = " here ";
                if (siblingText.endsWith(" here")) splitString = " here";
                if (!siblingText.contains(splitString)) return;

                temp.appendSibling(new TextComponentString(TextFormatting.AQUA + siblingText.substring(0, siblingText.indexOf(splitString))));
                String command = "/find " + fullMessage.substring(fullMessage.lastIndexOf("\u2605") == -1 ?
                        1 : fullMessage.lastIndexOf("\u2605") + 1, fullMessage.indexOf("]"));

                ITextComponent hereComponent = new TextComponentString(TextFormatting.UNDERLINE + splitString + TextFormatting.RESET);
                hereComponent.getStyle().setHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
                temp.appendSibling(hereComponent);
                temp.appendSibling(new TextComponentString(TextFormatting.AQUA + siblingText.substring(siblingText.indexOf(splitString) + splitString.length())));
                textComponent.getSiblings().remove(sibling);
                textComponent.appendSibling(temp);
            }

        }
    }
}
