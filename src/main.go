package main

import "os"
import "flag"

func check(e error) {
    if e != nil {
        panic(e)
    }
}

func main() {
    var inputFile *os.File
    var outputFile *os.File
    var err error
    var inputFileName string

    outputFileName := flag.String("o", "", "Output file name. Default: stdout")
    flag.Parse()

    if len(flag.Args()) > 0 {
    	inputFileName = flag.Args()[0]
    	inputFile, err = os.Open(inputFileName)
        defer inputFile.Close()
    	check(err)
    } else {
    	inputFile = os.Stdin
    	inputFileName = ""
    }

    transformed := process(*inputFile)

    if *outputFileName == "" {
        outputFile = os.Stdout
    } else {
        outputFile, err = os.Open(inputFileName)
        defer outputFile.Close()
    }

    outputFile.Write(transformed)
}
