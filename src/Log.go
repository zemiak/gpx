package main

import "encoding/xml"

type Log struct {
	XMLName xml.Name `xml:groundspeak:log"`
	id string `xml:"id,attr"`
	date string `xml:"groundspeak:date"`
	logType string `xml:"groundspeak:type"`
	finder string `xml:"groundspeak:finder"`
	text string `xml:"groundspeak:text"`
}
