# import the necessary packages
from pyimagesearch.helpers import pyramid
from pyimagesearch.helpers import sliding_window
import argparse
import time
import cv2
 
# construct the argument parser and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required=True, help="Path to the image")
args = vars(ap.parse_args())
 
# load the image and define the window width and height
image = cv2.imread(args["image"],cv2.IMREAD_GRAYSCALE)
(winW, winH) = (200, 200)
# loop over the image pyramid
for a in range (0,2):
	for b in range (0,2):
		
		# loop over the sliding window for each layer of the pyramid
		for (x, y, window) in sliding_window(image, stepSize=24, windowSize=(winW, winH)):
			# if the window does not meet our desired window size, ignore it
			if window.shape[0] != winH or window.shape[1] != winW:
				continue
			if image[x,y] != 255:
				continue
			print (x,y)		
				# x, x+a y, y+b
			# THIS IS WHERE YOU WOULD PROCESS YOUR WINDOW, SUCH AS APPLYING A
			# MACHINE LEARNING CLASSIFIER TO CLASSIFY THE CONTENTS OF THE
			# WINDOW
			# since we do not have a classifier, we'll just draw the window
			clone = image.copy()
			cv2.rectangle(clone, (x, y), (x + winW, y + winH), (0, 255, 0), 2)
			cv2.imshow("Window", clone)
			cv2.waitKey(1)
			time.sleep(0.025)
		winW = winW + 20 # 1st loop 100 120 2nd loop 140 160
		print(winW)
	winH = winH + 20 # 1st loop 100 2nd loop 120
	print(winH)		