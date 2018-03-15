package main

type Waypoint struct {
	Lat string `xml:"lat,attr"`
	Lon string `xml:"lon,attr"`

	Time string `xml:"time"`
	Name string `xml:"name"`
	Desc string `xml:"desc"`
	Url string `xml:"url"`
	UrlName string `xml:"urlname"`
	Sym string `xml:"sym"`
	WpType string `xml:"type"`
	Cache GeoCache `xml:"groundspeak:cache"`
}
