#!/bin/bash

if [ ! -f target/gpx.jar ]
then
    echo Build the gpx.jar first
    exit 1
fi

cp gpx.bin.template target/gpx
echo "PAYLOAD:" >>target/gpx
cat target/gpx.jar >>target/gpx
