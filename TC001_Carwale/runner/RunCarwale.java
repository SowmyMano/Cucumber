package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
//import io.cucumber.testng.CucumberOptions.SnippetType;

@CucumberOptions(features = "src/test/java/feature/Carwale.feature", glue = "steps", monochrome = true)
//					, dryRun = true, snippets = SnippetType.CAMELCASE)

public class RunCarwale extends AbstractTestNGCucumberTests{

}
