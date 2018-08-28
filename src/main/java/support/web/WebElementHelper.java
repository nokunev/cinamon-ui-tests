package support.web;

import config.ApplicationProperties;
import config.webdriver.DriverBase;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static config.ApplicationProperties.ApplicationProperty.WAIT_TIMEOUT_SHT;
import static org.junit.jupiter.api.Assertions.fail;

public class WebElementHelper {
    private static final Logger logger = LoggerFactory.getLogger(WebElementHelper.class);

    public static boolean isElementDisplayed(By locator) {
        return isElementDisplayed(locator, ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
    }

    public static boolean isElementDisplayed(By locator, int timeOut) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), timeOut);
        try {
            WebElement element = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element.isDisplayed();
        } catch (NoSuchElementException | TimeoutException ne) {
            return false;
        } catch (StaleElementReferenceException ex) {
            return isElementDisplayed(locator, timeOut);
        }
    }

    public static WebElement waitForElement(By locator) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static WebElement waitForElement(By locator, int timeout) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), timeout);
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForInivsibilityOfElement(By locator) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
        return webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static List<WebElement> waitForElements(By locator) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
        return webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static WebElement waitForVisibility(By locator) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
        try {
            return webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException te) {
            logger.error(te.getMessage());
            fail("Locator '" + locator + "' not found after waiting for it's visibility");
        } catch (NoSuchElementException ne) {
            logger.error(ne.getMessage());
            fail("Locator '" + locator + "' not found, unable to locate it");
        } catch (Exception e) {
            logger.error(e.getMessage());
            fail("Locator '" + locator + "' not found");
        }
        return null;
    }

    public static void sendKeys(By locator, String text) {
        logger.info("Setting textbox value - " + text);
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
        WebElement webElement = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        webElement.clear();
        webElement.sendKeys(text);
    }

    public static void click(By locator) {
        scrollToElement(locator);
        waitForElementToBeClickable(locator).click();
    }

    public static void jsClick(By locator) {
        WebElement element = waitForElement(locator);
        jsClick(element);
    }

    public static void jsClick(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) DriverBase.getDriver();
        executor.executeScript("arguments[0].click();", element);
    }

    public static void selectByVisiableText(By locator, String value) {
        Select select = new Select(waitForElementToBeClickable(locator));
        select.selectByVisibleText(value);
    }

    public static String getSelectedValueFromDropdown(By locator) {
        Select select = new Select(waitForElementToBeClickable(locator));
        return select.getFirstSelectedOption().getText();
    }

    public static WebElement waitForElementToBeClickable(By locator) {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
        return webDriverWait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static String getText(By locator) {
        String result=waitForVisibility(locator).getText();
        if (result==null||"".equals(result)){
            return getAttribute(locator, "value");
        }
        return result;
    }

    public static String getAttribute(By locator, String attribute) {
        return waitForElement(locator).getAttribute(attribute);
    }

    public static String getValue(By locator) {
        return waitForVisibility(locator).getAttribute("value");
    }

    public static void setValueForCheckbox(By locator, boolean value) {
        if (isSelected(locator) != value) {
            waitForElement(locator).click();
        }
    }

    public static boolean isSelected(By locator) {
        return waitForElement(locator).isSelected();
    }

    public static void navigateToPage(String url) {
        logger.info("Navigating to: " + url);
        DriverBase.getDriver().get(url);
    }

    public static void refresh() {
        logger.info("Refresh page");
        DriverBase.getDriver().navigate().refresh();
    }

    public static Alert waitForAlert() {
        WebDriverWait webDriverWait = new WebDriverWait(DriverBase.getDriver(), ApplicationProperties.getInteger(WAIT_TIMEOUT_SHT));
        return webDriverWait.until(ExpectedConditions.alertIsPresent());
    }

    public static void scrollToElement(By locator) {
        String scrollToElement = "arguments[0].scrollIntoView(true);";
        ((JavascriptExecutor) DriverBase.getDriver()).executeScript(scrollToElement,  waitForElement(locator));
    }

    public static void scrollToElement(WebElement element) {
        String scrollToElement = "arguments[0].scrollIntoView(true);";
        ((JavascriptExecutor) DriverBase.getDriver()).executeScript(scrollToElement, element);
    }

    public static void scrollToCenterOfScreen(By locator) {
        waitForVisibility(locator);
        String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

        ((JavascriptExecutor) DriverBase.getDriver()).executeScript(scrollElementIntoMiddle, locator);
    }

    public static void mouseOver(By locator) {
        Actions action = new Actions(DriverBase.getDriver());
        action.moveToElement(waitForElement(locator)).build().perform();
    }

    public static void executeJS(String command) {
        JavascriptExecutor executor = (JavascriptExecutor) DriverBase.getDriver();
        executor.executeScript(command);
    }

    public static void sleep(int timeoutInMilisseconds) {
        try {
            Thread.sleep(timeoutInMilisseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}