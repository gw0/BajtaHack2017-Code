import cv2
from numpy import *
class ImageProcessing:

    # constructor
    def __init__(self, _image):
        self.image = _image

        # rotacija
        # for angle in range(-180, 180, 20):
        #rows, cols, dumy = self.image.shape
        #M = cv2.getRotationMatrix2D((cols / 2, rows / 2), 20, 1)
        #img = self.image.copy()
        #self.RotatedImage = cv2.warpAffine(img, M, (cols, rows))
        self.RotatedImage = self.image

        #filter the image with gaussian filter for noise reduction
        self.gauss =  cv2.GaussianBlur(self.RotatedImage.copy(),(9,9),0)

        #convert RGB to grayscale
        self.gray = cv2.cvtColor(self.gauss.copy(), cv2.COLOR_BGR2GRAY)

        #thresholding the image and show
        ret,self.threshold = cv2.threshold(self.gray.copy(),70,255,cv2.THRESH_BINARY)

        #perform morhological operation close if come pieces are cut
        #because of thresholding and show it
        kernel = ones((3,3), uint8)
        self.closed = 255 - self.threshold.copy()
        self.closed = cv2.morphologyEx(self.closed.copy(), cv2.MORPH_CLOSE, kernel)
        self.closed = 255 - self.closed
