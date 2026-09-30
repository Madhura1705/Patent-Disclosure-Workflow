package com.patent.disclosure;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PatentDisclosureSeleniumTest {

    private static WebDriver driver;

    private static WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8090";


    @BeforeAll
    static void setUp() {

        ChromeOptions options =
                new ChromeOptions();

        // Keep browser visible for local testing
        options.addArguments("--start-maximized");

        driver =
                new ChromeDriver(options);

        wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(10)
                );
    }


    // =========================================================
    // TEST 1 — Applicant submission page loads
    // =========================================================

    @Test
    void testApplicantPageLoads() {

        driver.get(BASE_URL);

        WebElement heading =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.xpath(
                                                "//h2[contains(text(),'Submit Patent Disclosure')]"
                                        )
                                )
                );

        assertTrue(
                heading.isDisplayed(),
                "Applicant submission page should be visible."
        );

        assertEquals(
                "Submit Patent Disclosure",
                heading.getText()
        );
    }


    // =========================================================
    // TEST 2 — Required field validation
    // =========================================================

    @Test
    void testRequiredFieldValidation() {

        driver.get(BASE_URL);

        WebElement submitButton =
                driver.findElement(
                        By.id("submitDisclosure")
                );

        submitButton.click();

        WebElement applicantName =
                driver.findElement(
                        By.id("applicantName")
                );

        String validationMessage =
                (String)
                        ((org.openqa.selenium.JavascriptExecutor) driver)
                                .executeScript(
                                        "return arguments[0].validationMessage;",
                                        applicantName
                                );

        assertTrue(
                validationMessage != null
                        && !validationMessage.isBlank(),
                "Required-field validation should be triggered."
        );
    }


    // =========================================================
    // TEST 3 — Complete applicant submission
    // =========================================================

    @Test
    void testCompleteDisclosureSubmission() {

        driver.get(BASE_URL);


        driver.findElement(
                By.id("applicantName")
        ).sendKeys(
                "Selenium Test Applicant"
        );


        driver.findElement(
                By.id("email")
        ).sendKeys(
                "selenium.test@example.com"
        );


        driver.findElement(
                By.id("department")
        ).sendKeys(
                "Computer Engineering"
        );


        driver.findElement(
                By.id("patentTitle")
        ).sendKeys(
                "Selenium Automated Patent System"
        );


        driver.findElement(
                By.id("patentDescription")
        ).sendKeys(
                "Automated Selenium test submission for the Patent Disclosure Workflow."
        );


        Path testFile =
                Paths.get(
                        "src",
                        "test",
                        "resources",
                        "test-document.pdf"
                ).toAbsolutePath();


        driver.findElement(
                By.id("document")
        ).sendKeys(
                testFile.toString()
        );


        driver.findElement(
                By.id("submitDisclosure")
        ).click();


        WebElement result =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.id("disclosureResult")
                                )
                );


        assertTrue(
                result.isDisplayed(),
                "Submission result should be displayed."
        );


        WebElement disclosureId =
                driver.findElement(
                        By.id("disclosureId")
                );


        assertTrue(
                disclosureId.getText()
                        .startsWith("PD-"),
                "A valid Disclosure ID should be generated."
        );


        WebElement status =
                driver.findElement(
                        By.id("disclosureStatus")
                );


        assertEquals(
                "SUBMITTED",
                status.getText()
        );
    }


    // =========================================================
    // TEST 4 — Status tracking
    // =========================================================

    @Test
    void testStatusTrackingPageLoads() {

        driver.get(
                BASE_URL + "/status.html"
        );


        WebElement heading =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.xpath(
                                                "//h2[contains(text(),'Track Patent Disclosure')]"
                                        )
                                )
                );


        assertTrue(
                heading.isDisplayed(),
                "Status tracking page should be visible."
        );


        WebElement input =
                driver.findElement(
                        By.id("disclosureIdInput")
                );


        assertTrue(
                input.isDisplayed(),
                "Disclosure ID input should be available."
        );


        WebElement checkButton =
                driver.findElement(
                        By.id("checkStatusButton")
                );


        assertTrue(
                checkButton.isDisplayed(),
                "Check Status button should be available."
        );
    }


    // =========================================================
    // CLEANUP
    // =========================================================

    @AfterAll
    static void tearDown() {

        if (driver != null) {

            driver.quit();

        }
    }
}