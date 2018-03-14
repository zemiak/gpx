package main

type Gpx struct {
	xsd string `xml:"xmlns:xsd,attr"`
	xsi string `xml:"xmlns:xsi,attr"`
	version string `xml:"version,attr"`
	creator string `xml:"creator,attr"`
	schemaLocation string `xml:"xsi:schemaLocation,attr"`
	xmlns string `xml:"xmlns,attr"`

	name string
	desc string
	author string
	email string
	time string
	keywords string
	bounds Bounds `xml:"bounds"`
	wpt []Waypoint `xml:"wpt"`
}
