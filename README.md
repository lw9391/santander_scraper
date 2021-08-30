# santander_scraper

Simple CLI app that signs into Santander bank account and prints a lists of accounts with their balances.

## Technologies
* Java 16
* Gradle 7.0.0
* Gson 2.8.7
* OkHttp 4.9.1
* Jsoup 1.14.1
* JUnit 5.7.1
* Mockito 3.11.2

## Launch
Navigate to the project folder and build it with a gradle wrapper using ./gradlew build command. 
After that you can unpack a zip file from build/distribution folder, navigate to the bin and start it by 
typing `SantanderScraper <nik> <password>`.
After a short while program will ask you to type sms token to confirm your signing in.
After your accounts are printed, you will be automaticly logged out.
