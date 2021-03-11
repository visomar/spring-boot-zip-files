/*
 * Copyright 2021 Vicente Soriano
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.zipping.zipping.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.zipping.zipping.exceptions.InvalidOperationException;
import com.example.zipping.zipping.service.ZipService;

@Service
public class ZipServiceImpl implements ZipService {

  private static final String NO_FILES_ERROR = "No files provided";
  private static final String FILE_CANNOT_HANDLE_ERROR = "One or more files cannot be processed";
  private static final String FILE_NOT_FOUND_ERROR = "File not found";

  @Override
  public byte[] zipFiles(Set<MultipartFile> files) {

    if (files.isEmpty()) {
      throw new InvalidOperationException(NO_FILES_ERROR);
    }

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos); ) {

      int count = 0;
      for (MultipartFile file : files) {
        InputStream is = file.getInputStream();
        String fileName =
            Optional.ofNullable(file.getOriginalFilename()).orElse(String.valueOf(++count));

        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = is.read(bytes)) >= 0) {
          zos.write(bytes, 0, length);
        }
        zos.closeEntry();
        is.close();
      }

      return baos.toByteArray();
    } catch (FileNotFoundException e) {
      throw new InvalidOperationException(FILE_NOT_FOUND_ERROR + " " + e.getLocalizedMessage());
    } catch (IOException e) {
      throw new InvalidOperationException(FILE_CANNOT_HANDLE_ERROR + " " + e.getLocalizedMessage());
    }
  }
}
