package ir.mehran1022.supervisory.utility;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public final class CharacterUtils {

    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    public String generateRandom(int length) {
        StringBuilder character = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            character.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return character.toString();
    }
}