import "encoding/xml"

type Attribute struct {
	desc string `xml:",content"`
	id string `xml:"id,attr"`
	inc string `xml:"inc,attr"`
}