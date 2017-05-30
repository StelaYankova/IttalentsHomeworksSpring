package com.IttalentsHomeworks.controller;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.IttalentsHomeworks.Exceptions.InvalidFilesExtensionInZIP;

/**
 * This utility extracts files and directories of a standard zip file to a
 * destination directory.
 * 
 * @author www.codejava.net
 *
 */
public class Unzipper {
	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;
	String zipFilePath;
	String destDirectory;

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified
	 * by destDirectory (will be created if does not exists)
	 * 
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	public Unzipper(String zipFilePath, String destDirectory) {
		this.zipFilePath = zipFilePath;
		this.destDirectory = destDirectory;
	}

	public boolean areExtensionsValid(ZipInputStream zipIn) throws IOException {

		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			if (!entry.isDirectory()) {
				String fileExtension = entry.getName().substring(entry.getName().length() - 3,
						entry.getName().length());
				if (!fileExtension.equals("txt")) {
					return false;
				}
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		return true;
	}

	public void unzip(String zipFilePath, String destDirectory) throws IOException, InvalidFilesExtensionInZIP {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();		
		//zipIn = new ZipInputStream(new FileInputStream(zipFilePath));

		if (areExtensionsValid(zipIn)) {
			zipIn.close();
			zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
			entry = zipIn.getNextEntry();
			while (entry != null) {
				String filePath = destDirectory + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					extractFile(zipIn, filePath);
					zipIn.closeEntry();
				} else {
					// if the entry is a directory, make the directory
					File dir = new File(filePath);
					dir.mkdir();
				}
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
		} else {
			throw new InvalidFilesExtensionInZIP("Valid file extension is .txt");
		}
			zipIn.close();
		
	}

	/**
	 * Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}