package net.ice.relic.engine.test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestOBF {
    // XOR function for obfuscating the content
    public static byte[] xorWithKey(byte[] data, byte key) {
        for (int i = 0; i < data.length; i++) {
            data[i] ^= key;
        }
        return data;
    }

    // Method to write the model file with optional XOR obfuscation
    public static void writeModelFile(String filePath, String jsonContent, byte xorKey) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            // Write header (Magic bytes + version)
            bos.write("RM01".getBytes(StandardCharsets.UTF_8));  // Magic identifier as raw bytes
            bos.write(1);  // Version 1 (can increment in the future)

            // Convert JSON content to bytes (UTF-8 encoding)
            byte[] jsonBytes = jsonContent.getBytes(StandardCharsets.UTF_8);

            // XOR obfuscate the bytes before writing (optional step)
            byte[] obfuscatedBytes = xorWithKey(jsonBytes, xorKey);

            // Write the length of the data (4 bytes for the length)
            bos.write(intToBytes(obfuscatedBytes.length));

            // Write the obfuscated data
            bos.write(jsonBytes);
        }
    }

    // Method to read the model file with optional XOR de-obfuscation
    public static String readModelFile(String filePath, byte xorKey) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            // Read and verify the header
            byte[] magicBytes = new byte[4];
            bis.read(magicBytes);
            String magic = new String(magicBytes, StandardCharsets.UTF_8);
            if (!"RM01".equals(magic)) {
                throw new IOException("Invalid file format");
            }

            // Read the version (we can use this for future compatibility)
            bis.read();  // Read 1 byte for the version (no need to store it)

            // Read the length of the data (4 bytes for length)
            byte[] lengthBytes = new byte[4];
            bis.read(lengthBytes);
            int length = bytesToInt(lengthBytes);

            // Read the obfuscated data
            byte[] obfuscatedBytes = new byte[length];
            bis.read(obfuscatedBytes);


            // De-obfuscate the data using XOR
            byte[] jsonBytes = xorWithKey(obfuscatedBytes, xorKey);

            //System.out.println(Arrays.toString(jsonBytes));

            // Convert the byte array back to a string (UTF-8 decoding)
            return new String(jsonBytes, StandardCharsets.UTF_8);
        }
    }

    // Utility methods to convert int to byte array and vice versa
    public static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static int bytesToInt(byte[] bytes) {
        return (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | (bytes[3]);
    }

    public static void main(String[] args) throws IOException {
        String json = "{ \"vertices\": [0.1, 0.2, 0.3], \"normals\": [0.0, 1.0, 0.0], \"texture\": \"someTexture.png\" }";
        String filePath = "model.rm";
        byte xorKey = (byte) 0xAA;  // XOR key for obfuscation

        // Write the model to file with XOR obfuscation
        writeModelFile(filePath, json, xorKey);

        // Read the model back from the file and de-obfuscate
        String loadedJson = readModelFile(filePath, xorKey);

    }
}
