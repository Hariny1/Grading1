# USAGE
# python pyramid.py --image images/adrian_florida.jpg 

# import the necessary packages
from pyimagesearch.helpers import pyramid
from skimage.transform import pyramid_gaussian
import argparse
import cv2

# construct the argument parser and parse the arguments
ap = argparse.ArgumentParser()
# DO NOT GIVE SCALE TO BE 1............
ap.add_argument("-i", "--image", required=True, help="Path to the image")
ap.add_argument("-s", "--scale", type=float, default=1.5, help="scale factor size")
#usage is python pyramid.py --image images/watch.jpeg
args = vars(ap.parse_args())


# load the image
image = cv2.imread(args["image"],cv2.IMREAD_GRAYSCALE)

# METHOD #1: No smooth, just scaling.
# loop over the image 
#pyramid
#for (i, resized) in enumerate(pyramid(image, scale=args["scale"])):
	# show the resized image
	#cv2.imshow("Layer {}".format(i + 1), resized)
	#cv2.waitKey(0)	
	''' Remove the cv2.waitKey(0)
	 so that the whole process is not repeated twice
	 but when removed only 3 layers are always shown - Figure out the error'''

# close all windows
cv2.destroyAllWindows()

# METHOD #2: Resizing + Gaussian smoothing.
'''for (i, resized) in enumerate(pyramid_gaussian(image, downscale=2)):
	# if the image is too small, break from the loop
	if resized.shape[0] < 30 or resized.shape[1] < 30:
		break
		
	# show the resized image
	cv2.imshow("Layer {}".format(i + 1), resized)
	cv2.waitKey(0)'''
