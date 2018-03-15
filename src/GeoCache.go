package main

type GeoCache struct {
	Id string `xml:"id,attr"`
	Available string `xml:"available,attr"`
	Archived string `xml:"archived,attr"`
	Xmlns string `xml:"xmlns:groundspeak,attr"`

	Name string `xml:"groundspeak:name"`
	PlacedBy string `xml:"groundspeak:placed_by"`
	Owner string `xml:"groundspeak:owner"`
	CacheType string `xml:"groundspeak:type"`
	Container string `xml:"groundspeak:container"`
	Attributes []Attribute `xml:"groundspeak:attributes"`
	Difficulty string `xml:"groundspeak:difficulty"`
	Terrain string `xml:"groundspeak:terrain"`
	Country string `xml:"groundspeak:country"`
	State string `xml:"groundspeak:state"`
	ShortDescription Description `xml:"groundspeak:short_description"`
	LongDescription Description `xml:"groundspeak:long_description"`
	Hint string `xml:"groundspeak:encoded_hints"`
	Logs []Log `xml:"groundspeak:logs"`
	TravelBugs []Travelbug `xml:"groundspeak:travelbugs"`
}
