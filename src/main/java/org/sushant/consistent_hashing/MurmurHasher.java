package org.sushant.consistent_hashing;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class MurmurHasher {
    public static int hash(String data) {
        return Hashing.murmur3_32_fixed().hashString(data, StandardCharsets.UTF_8).asInt();
    }
}
