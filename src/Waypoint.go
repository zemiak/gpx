package main

type Waypoint struct {
	time string
	name string
	desc string
	url string
	urlname string
	sym string
	wpType string `xml:"type"`
	cache Cache `xml:"groundspeak:cache"`
}
