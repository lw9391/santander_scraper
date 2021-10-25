# santander_scraper

Simple CLI app that signs into Santander bank account and prints a lists of accounts with their balances.

## Technologies
* Java 17
* Gradle 7.2.0

## Launch
Use the provided quickrun.sh file to build, unpack and run instantly providing your credentials as command line
arguments`./quickrun.sh <nik> <password>`. If you provide more than 2 args,the script will ignore them. In case
no arguments are provided, the script will just build and unpack app in the application directory.
You can use quickrun.sh each time you want to run the application even if it's already built. 

After a short while, the program will ask you to type a sms token to confirm your signing in.
After your accounts are printed, you will be automatically logged out.

You can also build project classic way using `./gradlew build` command in console in the project folder and unzipping it
manually from build/distribution. Run the application using the `SantanderScraper` script with your account number 
and password as command line arguments. Script is located in `SantanderScraper-1.0-SNAPSHOT/bin` in whatever location 
you unzip it. 

### Acceptance Test
`src/test/resources/scraper/credentials.sample` contains an example file with credentials that is need for running acceptance
test. Remove `.sample` part from filename and provide your bank credentials according to template. Git won't track
`credentials` file. 