@echo off

if not  exist %HOME%\Dropbox\garmin\gpx\*.gpx (
    echo Please, create folder %HOME%\Dropbox\garmin\gpx\ and copy some GPX files there
    exit
)

if exist %HOME%\Dropbox\garmin\all.ggz del /f %HOME%\Dropbox\garmin\all.ggz

call mvn package
java -jar target/gpx-1.0-SNAPSHOT.jar -o %HOME%\Dropbox\garmin\all.ggz %HOME%\Dropbox\garmin\gpx\*.gpx
