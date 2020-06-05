Feature: TC001_Carwale Implementation

Scenario: Print the details under Overview for the chosen car

Given Launch the application
And Click on Used
And Select the City as Chennai
And Select budget min (8L) and max(12L) and Click Search
And Select Cars with Photos under Only Show Cars With
And Select Manufacturer as Hyundai --> Creta
And Select Fuel Type as Petrol
And Select Best Match as KM: Low to High
And Validate the Cars are listed with KMs Low to High
And Add the least KM ran car to Wishlist
When Go to Wishlist and Click on More Details
Then Print all the details under Overview in the Same way as displayed in application 
And Close the browser