Ripped by Qw2, but many thanks to:
KillzXGaming and all the contributors to Switch-Toolbox, especially in relation to .mc / .txtg
Watertoon for their MC decompressor
Credit not necessary.

Notes:
 - In the DAE file(s), the UV maps are separated as alternate meshes (Often the correct texture will be either normal/specular or AO). However, the FBX(s) has all the UVs merged, so you can use that if you'd rather not combine them manually.

 - Textures with endings "_a", "_g", "_b", "_r" and the like are split textures as the primary texture in question stores data in separate channels. When extensions are combined (e.g. "_a_g_b") this means that those channels were identical for that image, i.e. in that case the alpha, green, and blue channel store the same data in that image.