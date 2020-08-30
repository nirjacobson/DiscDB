package com.nirjacobson.discdb.util;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.io.IOUtils;

public class XMCDImporter {

  public static class SubmitFiles extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
      try {
        final URL url = new URL("http://localhost:8888/api/v1.0/xmcd");
        final URLConnection con = url.openConnection();
        final HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        http.setFixedLengthStreamingMode(attr.size());
        http.setRequestProperty("Content-Type", "application/octet-stream");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
          Files.copy(file, os);
        }
        try (InputStream is = http.getInputStream()) {
        } catch (final IOException pE) {
          final StringWriter writer = new StringWriter();
          IOUtils.copy(http.getErrorStream(), writer, "UTF-8");
          System.err.println(String.format("%s (%s)", file.getFileName(), writer.toString()));
        }
      } catch (final Exception pE) {
        pE.printStackTrace();
        return TERMINATE;
      }

      return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
      System.err.println(exc);
      return TERMINATE;
    }
  }

  public static void main(String[] args) throws IOException {
    Files.walkFileTree(
        Paths.get("/Users/nir/Downloads/freedb-complete-20200501"), new SubmitFiles());
  }
}
