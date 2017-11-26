# PossibleChar.py

import cv2
import numpy as np
import math

class PossibleChar:

    def __init__(self, _contour):
        self.contour = _contour

        self.boundingRect = cv2.boundingRect(self.contour)

        [intX, intY, intWidth, intHeight] = self.boundingRect

        self.intBoundingRectX = intX
        self.intBoundingRectY = intY
        self.intBoundingRectWidth = intWidth
        self.intBoundingRectHeight = intHeight

        self.intBoundingRectArea = self.intBoundingRectWidth \
                                   * self.intBoundingRectHeight
        self.fltAspectRatio = float(self.intBoundingRectWidth) / \
                              float(self.intBoundingRectHeight)
    # end constructor

# end class
