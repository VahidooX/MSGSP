# MS-GSP: Multi-Support Generalized Sequential Patterns

This is an implementation of **MS-GSP** algorithm in Java. It can extract frequent patterns from a bunch of sequences. MS-GSP is introduced in the following book for mining sequential patterns:

Bing Liu, "[Web Data Mining - Exploring Hyperlinks, Contents, and Usage Data](http://www.springer.com/us/book/9783642194597)", P 43-49, Springer, 2011.

MS-GSP is an extension of GSP algorithm which can support multiple minimum supports. Original GSP is proposed in the following paper:

Ramakrishnan Srikant and Rakesh Agrawal, "[Mining Sequential Patterns: Generalizations and Performance Improvements](https://link.springer.com/chapter/10.1007%2FBFb0014140?LI=true)", EDBT, 1996.

### How to run?
To run the code, you should give the input and output files to the main file (MSGSP.Program.java) with the command line arguments as:
```
java Program <parameter file> <data file> <output file>
```
Examples of the parameter and input files are given along with the code. The formats are straightforward.
These examples can be given to the program as:
```
java Program params.txr inputdata.txt out.txt
```

Note: This code is written by Me and Nick Green.