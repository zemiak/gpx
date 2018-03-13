package main

import (
	"bytes"
	"encoding/xml"
	"fmt"
)

type Node struct {
	XMLName xml.Name
	Attrs   []xml.Attr `xml:"-"`
	Content []byte     `xml:",innerxml"`
	Nodes   []Node     `xml:",any"`
}

func (n *Node) UnmarshalXML(d *xml.Decoder, start xml.StartElement) error {
	n.Attrs = start.Attr
	type node Node

	return d.DecodeElement((*node)(n), &start)
}

func walkit(inputFile os.File, outputFileName string, inputFileName string) {
	buf := bytes.NewBuffer(data)
	dec := xml.NewDecoder(buf)

	var n Node
	err := dec.Decode(&n)
	if err != nil {
		panic(err)
	}

	walk([]Node{n}, func(n Node) bool {
		if n.XMLName.Local == "p" {
			fmt.Println(string(n.Content))
			fmt.Println(n.Attrs)
		}
		return true
	})
}

func walk(nodes []Node, f func(Node) bool) {
	for _, n := range nodes {
		if f(n) {
			walk(n.Nodes, f)
		}
	}
}
