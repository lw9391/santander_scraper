# santander_scraper

Simple CLI app that signs into Santander bank account and prints a lists of accounts with their balances.

## Technologies
* Java 17
* Gradle 7.2.0

## Acceptance test
Tu run acceptance tests put your credentials in a credentials.txt file in project directory and use 
`./gradlew runAcceptanceTests` command. Write your account number in the first line of the file and password
in the second line. You won't see any information asking for sms code during that test so remember to type it in after
initialization.
Remember to remove credentials after finishing tests from that text file as it is not safe to store 
them in plaintext.
Acceptance tests aren't run when building the project.

## Launch
For quick build and unpack simply launch gradlew script. Script will unpack the distribution in 
`application` directory by default.
You can also do it classic way using `./gradlew build` command in console in the project folder and unzipping it 
manually from build/distribution.
Start the application using SantanderScraper script with your account number and password as command line arguments
`SantanderScraper <nik> <password>`. Script is located in application/SantanderScraper-1.0-SNAPSHOT/bin.
After a short while program will ask you to type sms token to confirm your signing in.
After your accounts are printed, you will be automatically logged out.