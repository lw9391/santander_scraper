# santander_scraper

Simple CLI app that signs into Santander bank account and prints a lists of accounts with their balances.

## Technologies
* Java 17
* Gradle 7.2.0

## Launch
### Build
#### Linux
Use provided quickbuild.sh file to unpack and navigate to bin directory instantly `source quickbuild.sh`
#### Windows
Launch gradlew script. Script will unpack the distribution in `application` directory by default. Navigate to bin
directory.

You can also build project classic way using `./gradlew build` command in console in the project folder and unzipping it 
manually from build/distribution.

### Running
Start the application using SantanderScraper script with your account number and password as command line arguments
`SantanderScraper <nik> <password>`. Script is located in application/SantanderScraper-1.0-SNAPSHOT/bin.
After a short while program will ask you to type sms token to confirm your signing in.
After your accounts are printed, you will be automatically logged out.