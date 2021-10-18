#!/bin/sh
./gradlew
cd application/SantanderScraper-1.0-SNAPSHOT/bin || exit

if [ "$#" -lt 2 ]
  then
    echo "Build and unpack finished. Credentials not supplied. To build and run provide credentials as arguments ./SantanderScraper <nik> <password>"
    exit
fi

./SantanderScraper "$1" "$2"