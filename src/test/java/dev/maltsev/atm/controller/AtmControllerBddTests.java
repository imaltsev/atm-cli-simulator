package dev.maltsev.atm.controller;

import dev.maltsev.atm.util.Loggable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AtmControllerBddTests.Config.class})
public class AtmControllerBddTests implements Loggable {

    @Autowired
    private AtmController atmController;

    @ParameterizedTest
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @ValueSource(
            strings = {
                    "scenario-readme",
                    "scenario-circle-deposit",
                    "scenario-deposit-then-withdraw",
                    "scenario-circle-transfer"
            })
    public void test(String directory) throws IOException {
        File input = ResourceUtils.getFile("src/test/resources/" + directory + "/input.txt");
        File expectedOutput = ResourceUtils.getFile("src/test/resources/" + directory + "/output.txt");
        assertTrue(input.exists());
        assertTrue(expectedOutput.exists());

        String expectedResult = new String(Files.readAllBytes(Paths.get(expectedOutput.getPath()))).trim();

        InputStream in = new FileInputStream(input);
        OutputStream out = new ByteArrayOutputStream();

        atmController.doProcess(in, out);

        String actualResult = out.toString().trim();

        Assertions.assertEquals(expectedResult, actualResult);
    }

    @ComponentScan(basePackages = {"dev.maltsev.atm"})
    public static class Config {

    }
}
