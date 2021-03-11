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

package com.example.zipping.zipping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
class ZippingApplicationTests {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void contextLoads() {}

  @Test
  public void endpointAcceptsOneFile_andReturnsZip() throws Exception {

    // given
    MockMultipartFile file =
        new MockMultipartFile(
            "files", "example-file.txt", "text/plain", "this is some text".getBytes());

    // when
    MvcResult result =
        mockMvc.perform(multipart("/api/zip").file(file)).andExpect(status().isOk()).andReturn();

    // then
    assertThat(result, is(notNullValue()));
    assertThat(result.getResponse().getContentType(), is("application/zip"));

    Map<String, String> entries = getZipEntriesFromResult(result);

    assertThat(entries.size(), is(1));
    assertThat(entries.keySet(), containsInAnyOrder("example-file.txt"));
    assertThat(entries.values(), containsInAnyOrder("this is some text"));
  }

  @Test
  public void endpointAcceptsMultipleFiles_andReturnsZip() throws Exception {

    // given
    MockMultipartFile file1 =
        new MockMultipartFile("files", "example-1.txt", "text/plain", "yepe".getBytes());
    MockMultipartFile file2 =
        new MockMultipartFile("files", "example-2.txt", "text/plain", "julepe".getBytes());

    // when
    MvcResult result =
        mockMvc
            .perform(multipart("/api/zip").file(file1).file(file2))
            .andExpect(status().isOk())
            .andReturn();

    // then
    assertThat(result, is(notNullValue()));
    assertThat(result.getResponse().getContentType(), is("application/zip"));

    Map<String, String> entries = getZipEntriesFromResult(result);

    assertThat(entries.size(), is(2));
    assertThat(entries.keySet(), containsInAnyOrder("example-1.txt", "example-2.txt"));
    assertThat(entries.values(), containsInAnyOrder("yepe", "julepe"));
  }

  @Test
  public void endpointCalledWithoutFiles_returnsError() throws Exception {

    mockMvc.perform(multipart("/api/zip")).andExpect(status().isBadRequest());
  }

  private Map<String, String> getZipEntriesFromResult(MvcResult result) throws IOException {
    ByteArrayInputStream bais =
        new ByteArrayInputStream(result.getResponse().getContentAsByteArray());
    ZipInputStream zis = new ZipInputStream(bais);

    Map<String, String> entries = new HashMap<>();

    ZipEntry entry;
    while ((entry = zis.getNextEntry()) != null) {
      byte[] byteBuffer = new byte[1024];
      int length = 0;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      while ((length = zis.read(byteBuffer)) != -1) {
        baos.write(byteBuffer, 0, length);
      }
      entries.put(entry.getName(), baos.toString());
    }

    return entries;
  }
}
