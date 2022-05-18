#!/bin/bash
#1 	Catchall for general errors
#2 	Misuse of shell builtins (according to Bash documentation)
#126 	Command invoked cannot execute
#127 	Command not found
#128 	Invalid argument to exit command
#128+n 	Fatal error signal "n"
#130 	Bash script terminated by Control-C
#255+ 	Exit status out of range

MIN_ARGS=4

E_ARGS=1
E_GENERIC=2

#check number of arguments
if [[  $# -lt $MIN_ARGS ]];then
	echo "[ERROR] args: given $#, required $MIN_ARGS" 1>&2
	exit $E_RGS
fi

dirin=$1
N=$2
suffix=$3
dirout=$4

#check if N is a positive number
if [[ $N = *[!0-9]* ]]; then #use if ! [[ $N = *(-?)[0-9]* ]] for positive and negative numbers
	echo "[ERROR]: $N is not a positive number" 1>&2
	exit $E_GENERIC
fi

#check if the path is absolute
if ! [[ "$dirin" = /* ]];then
	echo "[ERROR] directory '$dirin' must be absolute." 1>&2
    exit -2;
fi

#check if dirin is a directory and the user has execute permission
if ! [[ -d "$dirin" && -x "$dirin" ]]
then 
   echo [ERROR] cannot access to: $dirin, do you have execute permission?
   exit $E_GENERIC
fi

#missing other file invocation.
