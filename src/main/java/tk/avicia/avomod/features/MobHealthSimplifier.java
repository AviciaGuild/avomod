package tk.avicia.avomod.features;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Utils;

import java.util.stream.Collectors;

public class MobHealthSimplifier {
    @SubscribeEvent
    public void entityRender(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("readableHealth")) return;

        EntityLivingBase entity = event.getEntity();
        String tag = TextFormatting.getTextWithoutFormattingCodes(entity.getCustomNameTag());

        if (tag == null || !tag.startsWith("[|||||")) return;
        String health = tag.replaceAll("[\\[\\]|]", "");
        try {
            char lastChar = ' ';
            boolean red = false;
            int redCount = 0;
            int grayCount = 0;

            for (char s : entity.getCustomNameTag().toCharArray()) {
                if (((int) lastChar) == 167) {
                    if (s == '4' || s == 'c') {
                        red = true;
                    } else if (s == '8' || s == '0') {
                        red = false;
                    }
                } else if ((int) s != 167) {
                    if (red) {
                        redCount++;
                    } else {
                        grayCount++;
                    }
                }

                lastChar = s;
            }

            double percentRed = (redCount - 2.0) / (tag.length() - 2.0);
            String newHealth = Utils.parseReadableNumber(Double.parseDouble(health), 1);
            int newRedCount = (int) (percentRed * (newHealth.length() + 10));

            String newTagWithoutFormatting = String.format("|||||%s|||||", newHealth);
            StringBuilder newTag = new StringBuilder();

            for (String s : newTagWithoutFormatting.split("")) {
                if (newRedCount > 0) {
                    if (s.equals("|")) {
                        newTag.append(TextFormatting.RED).append(s);
                    } else {
                        newTag.append(TextFormatting.DARK_RED).append(s);
                    }
                    newRedCount--;
                } else {
                    if (s.equals("|")) {
                        newTag.append(TextFormatting.BLACK).append(s);
                    } else {
                        newTag.append(TextFormatting.DARK_GRAY).append(s);
                    }
                }
            }

            entity.setCustomNameTag(TextFormatting.DARK_RED + "[" + newTag + TextFormatting.DARK_RED + "]");
        } catch (Exception ignored) {

        }
    }

    @SubscribeEvent
    public void bossInfo(RenderGameOverlayEvent.BossInfo event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("readableHealth")) return;

        String bossbarText = TextFormatting.getTextWithoutFormattingCodes(event.getBossInfo().getName().getUnformattedText());
        if (bossbarText == null) return;

        int heartIconIndex = bossbarText.chars().boxed().collect(Collectors.toList()).indexOf(10084);
        if (heartIconIndex != -1) {
            String[] bossbarSplit = bossbarText.substring(0, heartIconIndex).split(" - ");
            if (bossbarSplit.length <= 1) return;

            String health = bossbarSplit[1];
            try {
                String nicerHealth = Utils.parseReadableNumber(Double.parseDouble(health), 1);
                String newBossbarText = event.getBossInfo().getName().getUnformattedText().replace(health, nicerHealth);
                event.getBossInfo().setName(new TextComponentString(newBossbarText));
            } catch (Exception ignored) {

            }
        }
    }
}
