#!/usr/bin/python

"""
all .png icons in drawable (day folder) are completely black
generate from these images in the night folder completely white icons
with the same size (64x64px)
"""


import os
from os.path import join
import cv2
import numpy as np

# newly defined colors
# 255, 255, 255 is white
# 0, 0, 0 is black
formerly_black = (255-84, 255-110, 255-122, 255)
formerly_white = (0, 0, 0, 255)

# define paths
res_path = './app/src/main/res'
day_icons_path, night_icons_path = join(res_path, 'drawable'), join(res_path, 'drawable-night')

png_files = [name for name in os.listdir(day_icons_path) if name.endswith('.png')]

for name in png_files:
    # read all og images as numpy array
    im = cv2.imread(join(day_icons_path, name), cv2.IMREAD_UNCHANGED) 

    # make black pixels to new color and other way round
    height, width, _ = im.shape

    for i in range(height):
        for j in range(width):
            if im[i, j, -1] != 0:
                rgb_colors = im[i, j, :3].sum()

                if rgb_colors == 0:
                    im[i, j] = formerly_black
                elif rgb_colors == 255*3:
                    im[i, j] = formerly_white 
 
    # write changed image (np array) to night folder file
    # and print that is writing image
    night_path = join(night_icons_path, name)   
    print(night_path)

    cv2.imwrite(night_path, im)

