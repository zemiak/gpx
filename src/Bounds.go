import "encoding/xml"

type Bounds struct {
	minlat string `xml:"minlat,attr"`
	minlon string `xml:"minlon,attr"`
	maxlat string `xml:"maxlat,attr"`
	maxlon string `xml:"maxlon,attr"`
}