package com.alpha.omega.core.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RandomPasswordGenerator {
    public static final String ALLOWED_SPL_CHARACTERS = "!@#$%^&*()_+";
    public static final String ERROR_CODE = "ERRONEOUS_SPECIAL_CHARS";
    public static final Random random = new SecureRandom();

    public static final String generateCommonsLang3Password() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }


    public static final String generateCommonsLang3Password(int max) {
        String upperCaseLetters = RandomStringUtils.random(max, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(max, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(max);
        //String specialChar = RandomStringUtils.random(max, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(max);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
                .limit(max)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }

    public static final String generateSecureRandomPassword() {
        Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2), Stream.concat(getRandomSpecialChars(2), Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false))));
        List<Character> charList = pwdStream.collect(Collectors.toList());
        Collections.shuffle(charList);
        String password = charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }

    public static final String generateRandomSpecialCharacters(int length) {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(33, 45)
                .build();
        return pwdGenerator.generate(length);
    }

    public static final String generateRandomNumbers(int length) {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(48, 57)
                .build();
        return pwdGenerator.generate(length);
    }

    public static final String generateRandomCharacters(int length) {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(48, 57)
                .build();
        return pwdGenerator.generate(length);
    }

    public static final String generateRandomAlphabet(int length, boolean lowerCase) {
        int low;
        int hi;
        if (lowerCase) {
            low = 97;
            hi = 122;
        } else {
            low = 65;
            hi = 90;
        }
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(low, hi)
                .build();
        return pwdGenerator.generate(length);
    }

    public static final Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
        IntStream characters = null;
        if (upperCase) {
            characters = random.ints(count, 65, 90);
        } else {
            characters = random.ints(count, 97, 122);
        }
        return characters.mapToObj(data -> (char) data);
    }

    public static final Stream<Character> getRandomNumbers(int count) {
        IntStream numbers = random.ints(count, 48, 57);
        return numbers.mapToObj(data -> (char) data);
    }

    public static final Stream<Character> getRandomSpecialChars(int count) {
        IntStream specialChars = random.ints(count, 33, 45);
        return specialChars.mapToObj(data -> (char) data);
    }
}
