from pyimagesearch.helpers import sliding_window
import argparse
import cv2
white=0
grey=0
ratio={}
listR =[]
count =0
#ap = argparse.ArgumentParser()
#ap.add_argument("-i","--image", required = True, help = "Path to image")
#args = vars(ap.parse_args())

image = cv2.imread("images/Cap2.png", cv2.IMREAD_GRAYSCALE)
newHeight = 200
newWidth = int(image.shape[1]*200/image.shape[0])

image = cv2.resize(image, (newWidth, newHeight))
clone = image.copy()
(winW, winH) = (56,67)

for(x,y,window) in sliding_window(image, stepSize =6,windowSize = (winW, winH)):
	count = count+1
	print (x,y)
	if window.shape[0]!= winH or window.shape[1]!= winW:
		continue
	#if x+winW < 195:
		#if y+winH<200:	
	#x = 123
	#y = 85		# 300 370
	for i in range(x ,x+winW): #(123 , 123+56):
		 # 265 370
		
		for j in range(y, y+winH):#(85 , 85+57):
			if (clone[i,j]==255):
				white= white+1
			else:
				grey = grey+1
	if grey!=0:				
		ratio = {'pixels' : (x, y),'Ratio': grey/white}
		
		print (ratio)
	
#print(ratio)	
print (count)		
#print(listR)
