Models do NOT have broken texture/UV mapping. Many of the models use mirrored repeat or seamless tiling. This allowed game developers to for example only draw half or quarter of a circle and mirror tiling will turn it into a full circle. Also material blending is lost as that was done in-game.

All models are vary large so you need to zoom out and/or scale the models down. IIRC scaling down to 0.01 is what I see people say most. I have NOT changed the geolocation for any of the models and this mean most of the models are quite far away from the center of the world. Most of the inside rooms look to be geolocated so you can move room to room like in game.

Textures. I don't know why some textures are being saved with transparency when in game they don't have transparency. Both Switch ToolBox and BMDView2 are saving the textures this way. Even Dolphin's bulit in texture dump is doing the same so I guess the texture do have an alpha layer but maybe the game dosen't use the alpha layer for transparency?

Other info:
Dolphin used to pull the D_MN08, D_MN08A, D_MN08B and D_MN08C directories out of the ISO than Switch ToolBox by KillzXGaming used open the .arc files inside of the extracted directories and save the models as .dae and save textures as .png.

Props and Objects:
I keep all of TPs objects extracted out as .bmd files This was done with yaz0dec and rarcdump before Switch Toolbox was a thing. The .bmd files where loaded into Switch Toolbox and saved as .dae with .png. 

AJD-262
3/18/24
