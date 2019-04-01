from pyimagesearch.helpers import sliding_window
import argparse
import cv2
white=0
grey=0
ratio=0.0
flag=0
listR =[]
ap = argparse.ArgumentParser()
ap.add_argument("-i","--image", required = True, help = "Path to image")
args = vars(ap.parse_args())

image = cv2.imread(args["image"], cv2.IMREAD_GRAYSCALE)
newHeight = 200
newWidth = int(image.shape[1]*200/image.shape[0])
image = cv2.resize(image, (newWidth, newHeight))
clone = image.copy()
(winW, winH) = (80,80)
for(x,y,window) in sliding_window(image, stepSize =12,windowSize = (winW, winH)):
	tempX = x
	tempY = y
	if window.shape[0]!= winH or window.shape[1]!= winW:
		continue	
	for i in range(y , y+winH):# 300 370
		if (clone[x,i] != 255):    #>255) and (clone[x,i]<200):
			flag = flag + 1
			break
		else:
			continue
	y = tempY		
	for i in range(x , x+winW):# 300 370
		if (clone[x,i] != 255):   #if (clone[i,y]>255) and (clone[i,y]<200):
			flag = flag + 1
			break
		else:
			continue
	x = tempX + winW
	for i in range(y , y+winH):# 300 370
		if (clone[x,i] != 255):   #if (clone[x,i]>255) and (clone[x,i]<200):
			flag = flag + 1
			break
		else:
			continue
	y = tempY + winH
	for i in range(x , x+winW):# 300 370
		if (clone[x,i] != 255):  #if (clone[i,y]>255) and (clone[i,y]<200):
			flag = flag + 1
			break
		else:
			continue				
	if flag != 0:
		winH = winH + 20
		winW = winW + 20
		# add to dictionary
		print(x,y,x+winW,y+winH)
#print(listR)
