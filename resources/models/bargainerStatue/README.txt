Ripped by Qw2, but many thanks to:
KillzXGaming and all the contributors to Switch-Toolbox, especially in relation to .mc / .txtg
Watertoon for their MC decompressor
Credit not necessary.

Notes:
 - In the DAE file(s), the UV maps are separated as alternate meshes (Often the correct texture will be either normal/specular or AO). However, the FBX(s) has all the UVs merged, so you can use that if you'd rather not combine them manually.
 
  - Yellow normal maps (or maps with yellow spots) are maps where the blue channel is likely encoded as roughness/specular for compression purposes. You can use your program of choice to either set the blue channel to the maximum value for all pixels, or separate the RGB channels and use just the red and green as X and Y while leaving the Z vector as 1. For example, can do this with Texture Remix (https://www.vg-resource.com/thread-26680.html) by assigning all the channels except blue (blue should be checked), and then exporting.