package main

import (
	"os"
	"fmt"
	"encoding/xml"
	"io"
)

func readFile(file os.File) []byte {
	var content []byte
	buf := make([]byte, 32*1024) // define your buffer size here.

    for {
        n, err := file.Read(buf)

        if n > 0 {
        	chunk := buf[:n]
        	content = append(content, chunk...)
        }

        if err == io.EOF {
            break
        }

        if err != nil {
            fmt.Printf("read %d bytes: %v", n, err)
            panic(err)
        }
    }

    return content
}

func process(inputFile os.File, outputFileName string, inputFileName string) {
	content := readFile(inputFile)

	var gpx Gpx
	e := xml.Unmarshal(content, &gpx)
	if e != nil {
		fmt.Printf("Error parsing XML")
		panic(e)
	}

	fmt.Printf("name %#v\n", gpx.name)
}
