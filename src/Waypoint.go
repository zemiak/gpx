import "encoding/xml"

type Waypoint struct {
	time string
	name string
	desc string
	url string
	urlname string
	sym string
	type string
	cache Cache `xml:"groundspeak:cache"`
}
