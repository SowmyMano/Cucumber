package steps;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Carwale {
	
	public ChromeDriver driver;
	ChromeOptions options;
	WebDriverWait wait;
	JavascriptExecutor executor;
	List<Integer> kmListSorted;

	@Given("Launch the application")
	public void launchApplication() {
		System.setProperty("webdriver.chrome.driver", "./chromedriver83/chromedriver.exe");
		
		//Disable notifications and launch the browser
		options = new ChromeOptions();
		options.addArguments("--disable-notifications");
		driver = new ChromeDriver(options);
																
		//Launch URL. maximize window and declare wait time
		driver.get("https://www.carwale.com/");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	@And("Click on Used")
	public void clickOnUsed() {
		driver.findElementByXPath("(//ul[@id='newUsedSearchOption']/li)[2]").click();
	}

	@And("Select the City as Chennai")
	public void selectCity() {
		driver.findElementById("usedCarsList").click();
		driver.findElementById("usedCarsList").sendKeys("Chennai", Keys.ENTER);
		wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[@class='ui-autocomplete ui-front ui-menu ui-widget ui-widget-content']//a[contains(@cityname,'chennai')]"))).click();
	}

	@And("Select budget min \\(8L) and max\\(12L) and Click Search")
	public void selectBudgetAndSearch() throws InterruptedException {
		driver.findElementByXPath("//ul[@id='minPriceList']/li[text()='8 Lakh']").click();
		driver.findElementByXPath("//ul[@id='maxPriceList']/li[text()='12 Lakh']").click();
		Thread.sleep(2000);
		driver.findElementById("btnFindCar").click();
		Thread.sleep(4000);
	}

	@And("Select Cars with Photos under Only Show Cars With")
	public void selectCarsWithPhotos() throws InterruptedException {
		driver.findElementByName("CarsWithPhotos").click();
		Thread.sleep(2000);
	}

	@And("Select Manufacturer as Hyundai --> Creta")
	public void selectManufacturer() throws InterruptedException {
		WebElement element = driver.findElementByXPath("//li[@data-manufacture-en='Hyundai']");
		executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", element);
		Thread.sleep(2000);
		WebElement subElement = driver.findElementByXPath("//span[text()='Creta']/parent::li");
		executor.executeScript("arguments[0].click();", subElement);
		Thread.sleep(2000);
	}

	@And("Select Fuel Type as Petrol")
	public void selectFuelType() throws InterruptedException {
		WebElement fuelElement = driver.findElementByXPath("//div[@name='fuel']");
		executor.executeScript("arguments[0].click();", fuelElement);
		WebElement fuelType = driver.findElementByXPath("//li[@name='Petrol']");
		executor.executeScript("arguments[0].click();", fuelType);
		Thread.sleep(2000);
	}

	@And("Select Best Match as KM: Low to High")
	public void selectBestMatch() throws InterruptedException {
		WebElement sortElement = driver.findElementById("sort");
		Select sortDropdown = new Select(sortElement);
		sortDropdown.selectByVisibleText("KM: Low to High");
		Thread.sleep(2000);
	}

	@And("Validate the Cars are listed with KMs Low to High")
	public void validateCars() throws InterruptedException {
		List<WebElement> kmList = driver.findElementsByXPath("//td[contains(@title,'km')]");
		List<Integer> kmListAsDisplayed = new ArrayList<Integer>();
		for (WebElement kmElement : kmList) {
			kmListAsDisplayed.add(Integer.parseInt(kmElement.getAttribute("title").replaceAll("\\D", "")));
		}
		System.out.println(kmListAsDisplayed);
		
		kmListSorted = new ArrayList<Integer>(kmListAsDisplayed);
		Collections.sort(kmListSorted);
		System.out.println(kmListSorted);
		
		boolean sorted = false;
		for(int i=0; i<kmListAsDisplayed.size(); i++) {
			if(kmListAsDisplayed.get(i)==kmListSorted.get(i)) {
				sorted = true;
			} else {
				sorted = false;
				break;
			}
		}
		
		if(sorted==true) {
			System.out.println("Cars are listed in order KM:Low to High");
		} else
			System.out.println("Cars are not listed in order KM:Low to High");
		Thread.sleep(3000);
	}

	@And("Add the least KM ran car to Wishlist")
	public void addCar() {
		int leastKm = kmListSorted.get(0);
		NumberFormat nFormat = NumberFormat.getInstance();
		nFormat.setGroupingUsed(true);
		String formattedKm = nFormat.format(leastKm);
		WebElement wishElement = driver.findElementByXPath("//span[contains(text(),\""+formattedKm+"\")]//ancestor::div[@class='card-detail-block']/preceding-sibling::div//span[contains(@class,'shortlist-icon')]");
		executor.executeScript("arguments[0].click()", wishElement);
	}

	@When("Go to Wishlist and Click on More Details")
	public void goToWishlist() throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[contains(@class,'action-box shortlistBtn')]"))).click();
		Thread.sleep(2000);
		driver.findElementByXPath("//a[contains(text(),'More details')]").click();
		
		Set<String> windowHandles = driver.getWindowHandles();
		List<String> handles = new ArrayList<String>(windowHandles);
		driver.switchTo().window(handles.get(1));
	}

	@Then("Print all the details under Overview in the Same way as displayed in application")
	public void printOverview() throws InterruptedException {
		List<WebElement> headingElements = driver.findElementsByXPath("//div[@id='overview']//div[@class='equal-width text-light-grey']");
		List<WebElement> valueElements = driver.findElementsByXPath("//div[@id='overview']//div[@class='equal-width dark-text']");
		Map<String, String> overview = new LinkedHashMap<String, String>();
		
		for(int i=0; i<headingElements.size(); i++) {
			overview.put(headingElements.get(i).getText(), valueElements.get(i).getText());
		}
		
		System.out.println("Car Overview");
		System.out.println("=============");
		for (Map.Entry<String, String> entry : overview.entrySet()) {
			System.out.println(entry.getKey()+" = "+entry.getValue());
		}
		Thread.sleep(3000);
	}

	@And("Close the browser")
	public void closeBrowser() {
		driver.quit();
	}
}