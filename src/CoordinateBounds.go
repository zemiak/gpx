package main

type CoordinateBounds struct {
	MinLat string `xml:"minlat,attr"`
	MinLon string `xml:"minlon,attr"`
	MaxLat string `xml:"maxlat,attr"`
	MaxLon string `xml:"maxlon,attr"`
}
