/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.yifan.rpc.compressor;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import top.yifan.constants.CompressorType;

import java.io.ByteArrayInputStream;

/**
 * bzip2 compressor, faster compression efficiency
 *
 * @author Star Zheng
 * @link https://commons.apache.org/proper/commons-compress/
 */
public class Bzip2 implements Compressor {

    @Override
    public byte compressorId() {
        return CompressorType.BZIP2.getCode();
    }

    @Override
    public byte[] compress(byte[] dataByteArr) {
        if (dataByteArr == null || dataByteArr.length == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (BZip2CompressorOutputStream cos = new BZip2CompressorOutputStream(out)) {
            cos.write(dataByteArr);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        // 这里需要特别注意：要在BZip2输出流关闭之后才能返回，因为BZip2在关闭时会写入特定的字节码，
        // 如果在close之前返回，则数据是不完整的，在解压时会报错
        return out.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] dataByteArr) {
        if (dataByteArr == null || dataByteArr.length == 0) {
            return new byte[0];
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(dataByteArr);
             BZip2CompressorInputStream unZip = new BZip2CompressorInputStream(in)) {
            byte[] buffer = new byte[2048];
            int n;
            while ((n = unZip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
