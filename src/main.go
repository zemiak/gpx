package main

import "os"
import "fmt"
import "flag"
import "encoding/xml"

func check(e error) {
    if e != nil {
        panic(e)
    }
}

func main() {
    args := os.Args[1:]
    
    if len(args) < 1 {
    	fmt.Printf("Usage: gpx [-o outputFileName] inputFileName\n")
        os.Exit(10)
    }

    if len(flag.Args()) > 0 {
    	inputFileName = flag.Args()[0]
    	inputFile, err := os.Open(inputFileName)
    	check(err)
    } else {
    	inputFile = os.Stdin
    }

    res := readAndProcess(inputFile)
}
