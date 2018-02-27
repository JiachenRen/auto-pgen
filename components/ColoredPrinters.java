package components;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

/**
 * Created by Jiachen on 2/26/18.
 * Host a number of colored printers that are used throughout the project.
 */
public class ColoredPrinters {
    public static ColoredPrinter boldBlack = new ColoredPrinter.Builder(1, false)
            .attribute(Ansi.Attribute.BOLD)
            .foreground(Ansi.FColor.BLACK)
            .build();
    public static ColoredPrinter boldGreen = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.GREEN)
            .attribute(Ansi.Attribute.BOLD)
            .build();
    public static ColoredPrinter cyan = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.CYAN)
            .build();
    public static ColoredPrinter magenta = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.MAGENTA)
            .build();
    public static ColoredPrinter boldYellow = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.YELLOW)
            .attribute(Ansi.Attribute.BOLD)
            .build();
    public static ColoredPrinter boldRed = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.RED)
            .attribute(Ansi.Attribute.BOLD)
            .build();
    public static ColoredPrinter boldBlue = new ColoredPrinter.Builder(1, false)
            .foreground(Ansi.FColor.BLUE)
            .attribute(Ansi.Attribute.BOLD)
            .build();
    public static ColoredPrinter normal = new ColoredPrinter.Builder(1, false)
            .build();
}
