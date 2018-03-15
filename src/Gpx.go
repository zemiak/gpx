package main

type Gpx struct {
	Xsd string `xml:"xmlns:xsd,attr"`
	Xsi string `xml:"xmlns:xsi,attr"`
	Version string `xml:"version,attr"`
	Creator string `xml:"creator,attr"`
	SchemaLocation string `xml:"xsi:schemaLocation,attr"`
	Xmlns string `xml:"xmlns,attr"`

	Name string `xml:"name"`
	Desc string `xml:"desc"`
	Author string `xml:"author"`
	Email string `xml:"email"`
	Time string `xml:"time"`
	Keywords string `xml:"keywords"`
	Bounds CoordinateBounds `xml:"bounds"`
	Wpt []Waypoint `xml:"wpt"`
}
