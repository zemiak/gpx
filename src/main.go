package main

import "os"
import "fmt"
import "flag"

func check(e error) {
    if e != nil {
        panic(e)
    }
}

func main() {
    var inputFile *os.File
    var err error
    var inputFileName string

    outputFileName := flag.String("o", "", "Output file name. Default: stdout")
    flag.Parse()

    fmt.Printf("param len %d\n", len(flag.Args()))

    if len(flag.Args()) > 0 {
    	inputFileName = flag.Args()[0]
    	inputFile, err = os.Open(inputFileName)
    	check(err)
    } else {
    	inputFile = os.Stdin
    	inputFileName = ""
    }

    process(*inputFile, *outputFileName, inputFileName)
}
