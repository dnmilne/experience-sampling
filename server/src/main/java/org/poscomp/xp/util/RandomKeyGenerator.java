package org.poscomp.xp.util;

import java.math.BigInteger;
import java.security.SecureRandom;


public class RandomKeyGenerator {

    private SecureRandom random = new SecureRandom();


    public String generate() {
        return new BigInteger(70, random).toString(32);
    }

    public String generate(int digits) {
        return new BigInteger(digits, random).toString(32);
    }

}
