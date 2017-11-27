#!/bin/bash

IMAGE_FILE=test-image.jpg
URL=https://d2.srm.bajtahack.si:16200
FILE_ID=1

# Allocate file on SRM (may fail if already allocated)
curl --insecure -X POST "${URL}/sys/file/alloc" --header "Content-Type: image/jpeg" -d $FILE_ID

# Push image to SRM
#curl --insecure -X PUT "${URL}/sys/file/${FILE_ID}/value" -T "${IMAGE_FILE}"

base64 $IMAGE_FILE > ${IMAGE_FILE}.txt
curl --insecure -X PUT "${URL}/sys/file/${FILE_ID}/value" -T "${IMAGE_FILE}.txt"
