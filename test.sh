#!/bin/sh

if [ ! -f $HOME/Dropbox/garmin/gpx/*.gpx ]
then
    echo Please, create folder $HOME/Dropbox/garmin/gpx and copy some GPX files there
    exit 1
fi

if [ -f $HOME/Dropbox/garmin/all.ggz ]
then
    rm -f $HOME/Dropbox/garmin/all.ggz
fi

mvn package || exit 10
echo Running
java -jar target/gpx-1.0-SNAPSHOT.jar -o $HOME/Dropbox/garmin/all.ggz $HOME/Dropbox/garmin/gpx/*.gpx
