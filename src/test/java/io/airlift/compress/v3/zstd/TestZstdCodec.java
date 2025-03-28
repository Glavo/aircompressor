/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.compress.v3.zstd;

import com.google.common.io.Resources;
import io.airlift.compress.v3.AbstractTestCompression;
import io.airlift.compress.v3.Compressor;
import io.airlift.compress.v3.Decompressor;
import io.airlift.compress.v3.HadoopCodecCompressor;
import io.airlift.compress.v3.HadoopCodecDecompressor;
import io.airlift.compress.v3.thirdparty.ZstdJniCompressor;
import io.airlift.compress.v3.thirdparty.ZstdJniDecompressor;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class TestZstdCodec
        extends AbstractTestCompression
{
    @Override
    protected boolean isMemorySegmentSupported()
    {
        return false;
    }

    @Override
    protected Compressor getCompressor()
    {
        return new HadoopCodecCompressor(new ZstdCodec(), new ZstdJavaCompressor());
    }

    @Override
    protected Decompressor getDecompressor()
    {
        return new HadoopCodecDecompressor(new ZstdCodec());
    }

    @Override
    protected Compressor getVerifyCompressor()
    {
        // Hadoop format is the standard Zstd framed format
        return new ZstdJniCompressor(3);
    }

    @Override
    protected Decompressor getVerifyDecompressor()
    {
        // Hadoop format is the standard Zstd framed format
        return new ZstdJniDecompressor();
    }

    @Test
    void testConcatenatedFrames()
            throws IOException
    {
        byte[] compressed = Resources.toByteArray(Resources.getResource("data/zstd/multiple-frames.zst"));
        byte[] uncompressed = Resources.toByteArray(Resources.getResource("data/zstd/multiple-frames"));

        byte[] output = new byte[uncompressed.length];
        getVerifyDecompressor().decompress(compressed, 0, compressed.length, output, 0, output.length);

        assertByteArraysEqual(uncompressed, 0, uncompressed.length, output, 0, output.length);
    }
}
