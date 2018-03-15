package main

import "encoding/xml"

type Log struct {
	XMLName xml.Name `xml:groundspeak:log"`
	Id string `xml:"id,attr"`
	Date string `xml:"groundspeak:date"`
	LogType string `xml:"groundspeak:type"`
	Finder string `xml:"groundspeak:finder"`
	Text string `xml:"groundspeak:text"`
}
