package main

type Attribute struct {
	Desc string `xml:",content"`
	Id string `xml:"id,attr"`
	Inc string `xml:"inc,attr"`
}
