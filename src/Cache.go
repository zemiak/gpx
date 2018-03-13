import "encoding/xml"

type Cache struct {
	id string `xml:"id,attr"`
	available string `xml:"available,attr"`
	archived string `xml:"archived,attr"`
	xmlns string `xml:"xmlns:groundspeak,attr"`

	name string `xml:"groundspeak:name"`
	placed_by string `xml:"groundspeak:placed_by"`
	owner string `xml:"groundspeak:owner"`
	type string `xml:"groundspeak:type"`
	container string `xml:"groundspeak:container"`
	attributes []Attribute `xml:"groundspeak:attributes"`
	difficulty string `xml:"groundspeak:difficulty"`
	terrain string `xml:"groundspeak:terrain"`
	country string `xml:"groundspeak:country"`
	state string `xml:"groundspeak:state"`
	short_description Description `xml:"groundspeak:short_description"`
	long_description Description `xml:"groundspeak:long_description"`
	hint string `xml:"groundspeak:container"`
	logs []Log `xml:"groundspeak:container"`
	travelbugs []Travelbug `xml:"groundspeak:travelbugs"`
}
