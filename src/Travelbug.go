package main

type Travelbug struct {
	id string `xml:"id,attr"`
	ref string `xml:"ref,attr"`
	name string `xml:"groundspeak:name"`
}
