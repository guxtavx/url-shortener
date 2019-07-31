package com.dugonski.urlshortener.impl.component;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
public class Shortener {

    public String transform(String value) {
        return Hashing.murmur3_32().hashString(value, Charset.defaultCharset()).toString();
    }

}
