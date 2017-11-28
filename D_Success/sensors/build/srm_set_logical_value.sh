#!/bin/bash

# A script for pushing a value into (an existing) logical string value
# on target SRM

# Abort on error
set -e

# Our "central" SRM
#URL=https://d2.srm.bajtahack.si:16200
URL=https://193.2.177.156:16200

VARIABLE_NAME=$1
VALUE=$2

if [[ -z "$VARIABLE_NAME" ]]; then
    echo "Missing variable name!"
    echo "Usage: $0 <variable_name> <value>"
    exit -1
fi

if [[ -z "$VALUE" ]]; then
    echo "Missing value!"
    echo "Usage: $0 <variable_name> <value>"
    exit -1
fi

# Send the value via PUT; the variable must have been created before
# on the target SRM via POST('/log/string/alloc', 'variable_name')
# (preferrably inside the init script on the SRM?)
curl --insecure -X PUT "${URL}/log/string/${VARIABLE_NAME}/value" -d "${VALUE}"
