package org.noear.solonjt.utils;

import java.util.Random;

public class CodeUtils {
    public static String codeByRandom(int len) {
        int TEMPLATE_SIZE = 62;
        char codeTemplate[] = {
                'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x',
                'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z',
                '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9'
        };
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(codeTemplate[random.nextInt(TEMPLATE_SIZE) % TEMPLATE_SIZE]);
        }

        return sb.toString();
    }
}
