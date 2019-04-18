package ca.brandonrichardson.messenger.common.keygen.strategy;

import java.util.Random;

@FunctionalInterface
public interface RandomGeneratorStrategy extends GeneratorStrategy {

    String generate(int keySize);

    static RandomGeneratorStrategy alphanumeric() {
        return keySize -> {
            Random charTypeChooser = new Random();
            Random numberChooser = new Random();
            Random characterChooser = new Random();
            Random caseChooser = new Random();

            char[] key = new char[keySize];
            for(int pos = 0; pos < keySize; pos++) {
                if(charTypeChooser.nextInt(2) == 0) {
                    if(caseChooser.nextInt(2) == 0) {
                        key[pos] = (char) (characterChooser.nextInt(26) + 65);
                    } else {
                        key[pos] = (char) (characterChooser.nextInt(26) + 97);
                    }
                } else {
                    key[pos] = (char) (48 + numberChooser.nextInt(10));
                }
            }

            return new String(key);
        };
    }

    static RandomGeneratorStrategy alphanumericLowercase() {
        return keySize -> alphanumeric().generate(keySize).toLowerCase();
    }

    static RandomGeneratorStrategy alphanumericUppercase() {
        return keySize -> alphanumeric().generate(keySize).toUpperCase();
    }

    static RandomGeneratorStrategy numeric() {
        return keySize -> {
            Random numberChooser = new Random();

            char[] key = new char[keySize];
            for(int pos = 0; pos < keySize; pos++) {
                key[pos] = (char) (48 + numberChooser.nextInt(10));
            }

            return new String(key);
        };
    }

    static RandomGeneratorStrategy alphabetic() {
        return keySize -> {
            Random characterChooser = new Random(), caseChooser = new Random();

            char[] key = new char[keySize];
            for(int pos = 0; pos < keySize; pos++) {
                if(caseChooser.nextInt(2) == 0) {
                    key[pos] = (char) (characterChooser.nextInt(26) + 65);
                } else {
                    key[pos] = (char) (characterChooser.nextInt(26) + 97);
                }
            }

            return new String(key);
        };
    }
}
