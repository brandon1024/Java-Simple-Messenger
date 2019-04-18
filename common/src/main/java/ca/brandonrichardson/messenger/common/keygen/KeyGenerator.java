package ca.brandonrichardson.messenger.common.keygen;

import ca.brandonrichardson.messenger.common.keygen.strategy.HashGeneratorStrategy;
import ca.brandonrichardson.messenger.common.keygen.strategy.RandomGeneratorStrategy;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

public class KeyGenerator {

	public static String generateKey(final int keySize, final RandomGeneratorStrategy strategy) {
		return strategy.generate(keySize);
	}

	public static String generateKey(final String input, final HashGeneratorStrategy strategy) throws NoSuchAlgorithmException {
		return strategy.generate(input.getBytes(StandardCharsets.UTF_8));
	}

	public static String generateKey(final Supplier<byte[]> inputSupplier, final HashGeneratorStrategy strategy) throws NoSuchAlgorithmException {
		return strategy.generate(inputSupplier.get());
	}
}