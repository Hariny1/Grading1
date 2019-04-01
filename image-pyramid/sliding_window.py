# import the necessary packages
from pyimagesearch.helpers import pyramid
from pyimagesearch.helpers import sliding_window
import argparse
import time
import cv2
import numpy as np
 
# construct the argument parser and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required=True, help="Path to the image")
args = vars(ap.parse_args())
stepSize = 0 
# load the image and define the window width and height
image = cv2.imread(args["image"],cv2.IMREAD_GRAYSCALE)
img = np.asarray(image)
#p = img.shape
#print (p)
 # imread loads the image
(winW, winH) = (80, 80)

        #Sliding window size
# loop over the image pyramid
for a in range (0,2):
	for b in range (0,2):
		for resized in pyramid(image):
			# loop over the sliding window for each layer of the pyramid
			for (x, y, window) in sliding_window(resized, stepSize=24, windowSize=(winW, winH)):
				# if the window does not meet our desired window size, ignore it
				# or check for the boundary conditions step2 and check if the pixel is grey or not step1 and then 
				#calculate the ratio.
				if window.shape[0] != winH or window.shape[1] != winW:
					continue
					#stepSize=stepSize-10
					#winW = winW - 20
					#winH = winH - 20
				
				'''
				Classifier for images that are required, is to be inserted here.
				Like for tick marks and comments that ae to be detected.
				NOW LOOK FOR THE CLASSIFIERS.
				'''
		 
				# since we do not have a classifier, we'll just draw the window
				
				clone = resized.copy()
				px = clone[x,y]
				print (px)
						
				cv2.rectangle(clone, (x, y), (x + winW, y + winH), (0, 225, 0), 2)
				cv2.imshow("Window", clone)
				cv2.waitKey(1)
				time.sleep(0.025)
				for i in range(y , y+winH):
					winW = 80
					for j in range(x , x+winW):
						if (clone[x,y] <= 255) and (clone[x,y] >= 200): # 255 is white
							continue
						else:
							# add coordinates to the dictionary and then visualise it
							clone = resized.copy()
							winW = winW + 10
							cv2.rectangle(clone, (x, y), (x + winW, y + winH), (0, 225, 0), 2)
							cv2.imshow("Window", clone)
							cv2.waitKey(1)
							time.sleep(0.025)

					winH = winH + 10	
				# Increase the window size after every iteration 
				# sliding windowshould increase too, to give better results.
				
			winW = winW + 20
		winH = winH + 20			
