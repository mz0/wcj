package net.x320.app;

import net.x320.list.LinkedList;

import static net.x320.utilities.StringUtils.join;
import static net.x320.utilities.StringUtils.split;
import static net.x320.app.MessageUtils.getMessage;

import org.apache.commons.text.WordUtils;

public class App {
    public static void main(String[] args) {
        LinkedList tokens;
        tokens = split(getMessage());
        String result = join(tokens);
        System.out.println(WordUtils.capitalize(result));
    }
}
