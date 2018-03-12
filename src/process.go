package main

import "os"
import "fmt"

func process(inputFile os.File, outputFileName string, inputFileName string) {
	fmt.Printf("processing %s --> %s ...\n", inputFileName, outputFileName)
}
