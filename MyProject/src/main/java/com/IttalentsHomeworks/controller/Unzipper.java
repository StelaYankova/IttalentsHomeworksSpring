package com.IttalentsHomeworks.controller;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.IttalentsHomeworks.Exceptions.InvalidFilesExtensionInZIP;
 
/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class Unzipper {
    /**
     * Size of the buffer to read/write data
     */
//	private static final String INPUT_ZIP_FILE = "/Users/Stela/Desktop/Archive.zip";
//	private static final String OUTPUT_FOLDER = "/Users/Stela/Desktop/statistics";
    private static final int BUFFER_SIZE = 4096;
    String zipFilePath;
    String destDirectory;
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
//    public static void main(String[] args) {
//    		Unzipper unZip = new Unzipper();
//    		try {
//				unZip.unzip(INPUT_ZIP_FILE, OUTPUT_FOLDER);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	
//	}
   
    public Unzipper(String zipFilePath, String destDirectory){
    	this.zipFilePath = zipFilePath;
    	this.destDirectory = destDirectory;
    }
    
    public boolean areExtensionsValid(ZipInputStream zipIn) throws IOException{
    	
        ZipEntry entry = zipIn.getNextEntry();

        // iterates over entries in the zip file
        while (entry != null) {
        	System.out.println("Entry is not null");
           // String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
            	System.out.println("Entry is not directory");
                // if the entry is a file, extracts it
            	String fileExtension = entry.getName().substring(entry.getName().length() - 3,
						entry.getName().length());
            	System.out.println("File extension is " + entry.getName().substring(entry.getName().length() - 3,
									entry.getName().length()));
            	System.out.println("Extension: " + fileExtension);
            	if(!fileExtension.equals("txt")){
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
        if(areExtensionsValid(zipIn)){
		zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();

			if (!entry.isDirectory()) {
				extractFile(zipIn, filePath);
				zipIn.closeEntry();
			} else {
				System.out.println("ITS DIRECTORY");
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
				// throw new InvalidFilesExtensionInZIP("Valid file extension is
				// .txt");
			}
			entry = zipIn.getNextEntry();

        }

        zipIn.close();
        }else{
        	throw new InvalidFilesExtensionInZIP("Valid file extension is .txt");
        }
    }
    
    /**
     * Extracts a zip entry (file entry)
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
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//
//public class Unzipper {
//	private static final String INPUT_ZIP_FILE = "/Users/Stela/Desktop/Archive.zip";
//	private static final String OUTPUT_FOLDER = "/Users/Stela/Desktop/statistics";
//
//	public static void main(String[] args) {
//		Unzipper unZip = new Unzipper();
//		unZip.unZipIt(INPUT_ZIP_FILE, OUTPUT_FOLDER);
//	}
//
//	/**
//	 * Unzip it
//	 * 
//	 * @param zipFile
//	 *            input zip file
//	 * @param output
//	 *            zip file output folder
//	 */
//	public void unZipIt(String zipFile, String outputFolder) {
//
//		byte[] buffer = new byte[1024];
//
//		try {
//
//			// create output directory is not exists
//			File folder = new File(OUTPUT_FOLDER);
//			if (!folder.exists()) {
//				folder.mkdir();
//			}
//
//			// get the zip file content
//			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
//			// get the zipped file list entry
//			ZipEntry zipEntry = zipInputStream.getNextEntry();
//			int i = 0;
//			while (zipEntry != null) {
//				System.out.println("INput " + (++i));
//				String fileName = zipEntry.getName();
//				File newFile = new File(outputFolder + File.separator + fileName);
//				if(!newFile.exists()){
//					newFile.mkdir();
//				}
//				System.out.println("file unzip : " + newFile.getAbsoluteFile());
//
//				// create all non exists folders
//				// else you will hit FileNotFoundException for compressed folder
//				//new File(newFile.getParent()).mkdirs();
//
//				FileOutputStream fos = new FileOutputStream(newFile);
//
//				int len;
//				while ((len = zipInputStream.read(buffer)) > 0) {
//					fos.write(buffer, 0, len);
//				}
//
//				fos.close();
//				zipEntry = zipInputStream.getNextEntry();
//			}
//
//			zipInputStream.closeEntry();
//			zipInputStream.close();
//
//			System.out.println("Done");
//
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
//}
