package me.ahmetmelihomerabdullah.textfileencoder.Utils;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FileEncryption {
    private static final byte[] salt = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    private static Cipher makeCipher(String pass, Boolean decryptMode) throws GeneralSecurityException{
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");

        if(decryptMode){
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
        }

        return cipher;
    }

    public static void encryptFile(String fileName, String pass)
            throws IOException, GeneralSecurityException{
        byte[] decData;
        byte[] encData;
        File inFile = new File(fileName);

        Cipher cipher = FileEncryption.makeCipher(pass, true);

        FileInputStream inStream = new FileInputStream(inFile);

        int blockSize = 8;

        int paddedCount = blockSize - ((int)inFile.length()  % blockSize );

        int padded = (int)inFile.length() + paddedCount;

        decData = new byte[padded];


        inStream.read(decData);

        inStream.close();

        for( int i = (int)inFile.length(); i < padded; ++i ) {
            decData[i] = (byte)paddedCount;
        }

        encData = cipher.doFinal(decData);


        File outFile = new File(fileName + ".encrypted");
        FileOutputStream outStream = new FileOutputStream(outFile);
        outStream.write(encData);
        outStream.close();
        FileZIP.compressFile(outFile.getAbsolutePath());
    }

    public static boolean testEncryptFile(String fileName, String pass)
            throws IOException, GeneralSecurityException{
        try {
            byte[] decData;
            byte[] encData;
            File inFile = new File(fileName);

            Cipher cipher = FileEncryption.makeCipher(pass, true);

            FileInputStream inStream = new FileInputStream(inFile);

            int blockSize = 8;

            int paddedCount = blockSize - ((int)inFile.length()  % blockSize );

            int padded = (int)inFile.length() + paddedCount;

            decData = new byte[padded];


            inStream.read(decData);

            inStream.close();

            for( int i = (int)inFile.length(); i < padded; ++i ) {
                decData[i] = (byte)paddedCount;
            }

            encData = cipher.doFinal(decData);


            File outFile = new File(fileName + ".encrypted");
            FileOutputStream outStream = new FileOutputStream(outFile);
            outStream.write(encData);
            outStream.close();
            outFile.delete();
            return true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File decryptFile(String fileName, String pass, String fileFormat)
            throws GeneralSecurityException, IOException{
        byte[] encData;
        byte[] decData;
        File inFile = new File(fileName);

        Cipher cipher = FileEncryption.makeCipher(pass, false);

        FileInputStream inStream = new FileInputStream(inFile);
        encData = new byte[(int)inFile.length()];
        inStream.read(encData);
        inStream.close();
        decData = cipher.doFinal(encData);

        int padCount = (int)decData[decData.length - 1];

        if( padCount >= 1 && padCount <= 8 ) {
            decData = Arrays.copyOfRange( decData , 0, decData.length - padCount);
        }

        fileName = fileName.replace(".encrypted", fileFormat);
        FileOutputStream target = new FileOutputStream(new File(fileName));
        target.write(decData);
        inFile.delete();
        target.close();
        return new File(fileName);
    }
}