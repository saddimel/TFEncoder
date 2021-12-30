package me.ahmetmelihomerabdullah.textfileencoder.Utils;

import me.ahmetmelihomerabdullah.textfileencoder.Controllers.TextEncodeController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileZIP {
    public static String unzippedFile = null;

    public static void compressFile(String sourceFile) throws IOException {
        File delete = new File(sourceFile);
        FileOutputStream fos = new FileOutputStream(sourceFile+".zip");

        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);

        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;

        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
        delete.delete();
        TextEncodeController.zippedFile = fileToZip;
    }

    public static boolean testCompressFile(String sourceFile) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(sourceFile+".zip");

            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(sourceFile);
            FileInputStream fis = new FileInputStream(fileToZip);

            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;

            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            File delete = new File(sourceFile+".zip");
            zipOut.close();
            fis.close();
            fos.close();
            delete.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean testUnzipFile(Path source, Path target) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                boolean isDirectory = false;
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }
                Path newPath = zipSlipProtect(zipEntry, target);
                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();
            File delete = new File(target.toString());
            delete.delete();
            return true;
        }
    }

    public static void unzipFolder(Path source, Path target) throws IOException {

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = false;

                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();

        }

    }

    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }
}