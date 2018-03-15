package main

type Travelbug struct {
	Id string `xml:"id,attr"`
	Ref string `xml:"ref,attr"`
	Name string `xml:"groundspeak:name"`
}
