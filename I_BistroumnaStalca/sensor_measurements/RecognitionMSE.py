import scipy.misc
from PIL import Image
from numpy import *
import cv2
from os import listdir
from os.path import isfile, join
import PossibleChar as pc
import ImageProcessing as ip

def DoRecognition():
    #constants for possible char detection
    MIN_ASPECT_RATIO = 0.25
    MAX_ASPECT_RATIO = 0.9 #high because of letter like A
    MIN_PIXEL_AREA_RATIO = 0.06 #0.008
    COST_RATIO = 0.3

    #import letters from the data set
    mypath='letters'
    onlyfiles = [ f for f in listdir(mypath) if isfile(join(mypath,f)) ]
    chars = empty(len(onlyfiles), dtype=object)
    map = empty(len(onlyfiles), dtype=object)
    for n in range(0, len(onlyfiles)):
      chars[n] = cv2.imread( join(mypath,onlyfiles[n]) )
      map[n] = onlyfiles[n][1]

    #convert letters to grayscale type
    for idx, value in enumerate(chars):
         chars[idx] = cv2.cvtColor(value, cv2.COLOR_BGR2GRAY)

    #reading of the images
    Image_path = 'C:\Users\Tadej\Documents\BajtaHack\Code_Recognition\Image\lahka.jpg'
    image = cv2.imread(Image_path)

    #process the image for recognition
    ProcessedImage = ip.ImageProcessing(image)

    #find contours of the objects
    contours, hierarchy = cv2.findContours(ProcessedImage.closed.copy(),
                                           cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)

    #sort the object by area so we know the sequence of the letters
    contours = sorted(contours, key = cv2.boundingRect, reverse = False)

    #get possible characters
    PossibleChar_index = []
    for idx, contour in enumerate(contours):
        PossibleChar = pc.PossibleChar(contour)
        if MAX_ASPECT_RATIO > PossibleChar.fltAspectRatio > MIN_ASPECT_RATIO:
            ratio = float(PossibleChar.intBoundingRectArea/(float(image.shape[0]*image.shape[1])))
            if ratio > MIN_PIXEL_AREA_RATIO:
                 PossibleChar_index.append(idx)

    #show possible characters
    Image_tmp = ProcessedImage.image.copy()
    for i in PossibleChar_index:
        x, y, w, h = cv2.boundingRect(contours[i])
        cv2.rectangle(Image_tmp, (x, y), (x + w, y + h), (0, 255, 0), 2)

    #generate empty array, has to be dependant on the size of the input image
    #preparation for the final image for recognition
    size = (image.shape[0]*7, image.shape[1]*7)
    image_recognition = ones(size)*255
    image_closing_copy = ProcessedImage.closed.copy()

    cow_plate = []

    #get letters
    for i in PossibleChar_index:
        x,y,w,h = cv2.boundingRect(contours[i])
        letter = image_closing_copy[y:y+h, x:x+w]

        cost = inf
        for idx, value in enumerate(chars):
            #refactor images to the same size
            temp = scipy.misc.imresize(value.copy(), (letter.shape[0], letter.shape[1]))

            #threshold because of the resizing
            ret, temp = cv2.threshold(temp.copy(), 70, 255, cv2.THRESH_BINARY)

            #calculate the cost function - MSE (mean squared error)
            current_cost = sum((letter.astype("float") - temp.astype("float")) ** 2)
            pixel_size = letter.shape[0] * letter.shape[1]
            current_cost /= float(pixel_size)

            #check if the current calculated cost is lower then the previous best one
            if current_cost < cost:
                #remember the recognized letter
                cost = current_cost
                recognized_letter = map[idx]

        cow_plate.append(recognized_letter)

    # delete all white space
    output = ''
    for letter in cow_plate:
        output += letter
    return output