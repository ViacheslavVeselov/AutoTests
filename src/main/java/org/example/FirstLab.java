package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FirstLab {
    private WebDriver chromeDriver; // веб-драйвер, який імітує користувача
    private static final String baseUrl = "https://leetcode.com"; // базова адреса сайту

    // Умови для відкриття браузера – один раз на початку тестування
    @BeforeClass(alwaysRun = true)
    public void setUp() {
        // Встановлення відповідної версії ChromeDriver
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\ADMIN\\Downloads\\chromedriver.exe");
        // Запуск драйвера
        ChromeOptions chromeOptions = new ChromeOptions();
        // Встановлення на весь екран
        chromeOptions.addArguments("--start-fullscreen");
        this.chromeDriver = new ChromeDriver(chromeOptions);
    }

    // Умови для переходу на початкову сторінку сайту – для кожного тесту
    @BeforeMethod
    public void preconditions() {
        // Відкриття початкової сторінки сайту
        chromeDriver.get(baseUrl);
    }

    // Закриття вікна браузера після завершення тестування
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        chromeDriver.quit();
    }

    // Знаходження елемента за його id
    @Test
    public void testHeaderExists() {
        WebElement exploreComponent = chromeDriver.findElement(By.id("explore"));
        Assert.assertNotNull(exploreComponent);
    }

    // Перевірка переходу на сторінку по натисканні на кнопку
    @Test
    public void testClickOnCreateAccount() {
        // Знаходження елемента по xpath
        WebElement createAccountButton = chromeDriver.findElement(By.xpath(
                "/html/body/div[2]/div/div[1]/div/div[1]/div[3]/div[2]/div/div/div[2]/div/a"
        ));
        // Перевірка
        Assert.assertNotNull(createAccountButton);
        createAccountButton.click();
        // Перевірка зміни сторінки
        Assert.assertNotEquals(chromeDriver.getCurrentUrl(), baseUrl);
    }

    // Знаходження searchField по tagName та перевірка на введення
    @Test
    public void testSearchFieldOnProblemsPage() {
        String problemsPageUrl = "/problemset/all/";
        chromeDriver.get(baseUrl + problemsPageUrl);
        // Знаходження елемента по tagName
        WebElement searchField = chromeDriver.findElement(By.tagName("input"));
        // Перевірка
        Assert.assertNotNull(searchField);
        // Виведення параметрів loginField
        System.out.println(String.format("Name attribute: %s", searchField.getAttribute("name")) +
                String.format("\nID attribute: %s", searchField.getAttribute("id")) +
                String.format("\nType attribute: %s", searchField.getAttribute("type")) +
                String.format("\nValue attribute: %s", searchField.getAttribute("value")) +
                String.format("\nPosition: (%d;%d)", searchField.getLocation().x, searchField.getLocation().y) +
                String.format("\nSize: %dx%d", searchField.getSize().height, searchField.getSize().width)
        );
        // Введення значення
        String inputValue = "sum";
        searchField.sendKeys(inputValue);
        // Перевірка тексту
        Assert.assertEquals(searchField.getAttribute("value"), inputValue);
        // Натискання Enter
        searchField.sendKeys(Keys.ENTER);
        // Перевірка зміни сторінки
        Assert.assertNotEquals(chromeDriver.getCurrentUrl(), problemsPageUrl);
    }

    // Перевірка роботи вкладок
    @Test
    public void testTabs() {
        // Перехід на сторінку interview-question
        String interviewQuestionPageUrl = "/discuss/interview-question";
        chromeDriver.get(baseUrl + interviewQuestionPageUrl);
        // Знаходження елемента по імені класу
        WebElement tab = chromeDriver.findElement(By.className("css-1pz7gg5"));
        // Знаходження елемента по css selector
        WebElement InterviewExperienceTabByCss = chromeDriver.findElement(
                By.cssSelector("a.css-1pz7gg5")
        );
        // Перевірка, що це один і той же елемент
        Assert.assertEquals(tab, InterviewExperienceTabByCss);

        // Перевірка, що при зміні вкладки, змінюється посилання на іншу вкладку
        for (int i = 0; i < 10; i++) {
            if (tab.getAttribute("href").equals("/discuss/interview-experience")) {
                tab.click();
                Assert.assertNotEquals(tab.getAttribute("href"), "/discuss/interview-experience");
            } else if (tab.getAttribute("href").equals("/discuss/interview-question")) {
                tab.click();
                Assert.assertNotEquals(tab.getAttribute("href"), "/discuss/interview-question");
            }
        }
    }
}
