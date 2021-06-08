package tk.avicia.avomod.events;

import tk.avicia.avomod.utils.Tuple;

public class ToolTipState {
    public static boolean isTooltipRendering = false;
    public static int toolTipX = 0;
    public static int toolTipWidth = 0;
    public static int toolTipY = 0;
    public static int toolTipHeight = 0;

    public static boolean areCoordinatesOverlappingTooltip(Tuple<Integer, Integer> coordinates){
        return  (toolTipX - 15 < coordinates.x && toolTipX + toolTipWidth > coordinates.x && toolTipY - 15 < coordinates.y && toolTipY + toolTipHeight > coordinates.y);
    }
}
