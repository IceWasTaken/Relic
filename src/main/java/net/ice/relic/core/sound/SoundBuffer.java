package net.ice.relic.core.sound;

import net.ice.relic.core.sound.openal.ALBuffer;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.stb.STBVorbis.*;

public class SoundBuffer {

    private ALBuffer buffer;
    private ShortBuffer data;

    public SoundBuffer(String filePath) {
        this.buffer = new ALBuffer();
        try(STBVorbisInfo info = STBVorbisInfo.malloc()) {
            this.data = readVorbisData(filePath, info);

            buffer.bufferData(info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, data, info.sample_rate());
        }
    }

    private ShortBuffer readVorbisData(String filePath, STBVorbisInfo info) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            long decode = stb_vorbis_open_filename(filePath, error, null);
            if(decode == 0) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decode, info);
            int channels = info.channels();
            int lengthSamples = stb_vorbis_stream_length_in_samples(decode);
            ShortBuffer pcm = stack.mallocShort(lengthSamples);
            pcm.limit(stb_vorbis_get_samples_short_interleaved(decode, channels, pcm) * channels);
            stb_vorbis_close(decode);
            return pcm;
        }
    }
}
