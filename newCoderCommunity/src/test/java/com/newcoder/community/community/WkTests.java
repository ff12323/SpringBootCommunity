package com.newcoder.community.community;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class WkTests {

    @Test
    public void f1() throws IOException {
        String cmd = "ipconfig";
        Process process = Runtime.getRuntime().exec(cmd);

        BufferedReader stdOut = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String s ;
        while( (s = stdOut.readLine()) != null){
            System.out.println(s);
        }
    }
}
