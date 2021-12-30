package me.ahmetmelihomerabdullah.textfileencoder.Utils;

public class SPN {
    public static String encryptWithSpn(String text, String key){
        int text_lenght = text.length();
        int key_lenght = key.length();

        char[] chars_text = new char[text_lenght];
        chars_text = text.toCharArray();

        char[] chars_key = new char[key_lenght];
        chars_key = key.toCharArray();

        String[] ascii_binary_text = new String[text_lenght];
        String[] ascii_binary_key = new String[key_lenght];

        String[] cipher_text = new String[text_lenght];

        for(int i = 0; i<text_lenght; i++){
            int temp = (int)chars_text[i];
            String result = Integer.toBinaryString(temp);
            while(result.length() < 8){
                result = "0" + result;
            }
            ascii_binary_text[i] = result;
        }

        for(int i = 0; i < key_lenght; i++){
            int temp = (int)chars_key[i];
            String result = Integer.toBinaryString(temp);
            while(result.length() < 8){
                result = "0" + result;
            }
            ascii_binary_key[i] = result;

        }

        for(int i = 0; i < key_lenght; i++){
            for(int j = 0; j<text_lenght; j++){
                String temp_char = "";
                for(int k = 0; k<=7; k++){
                    if((int)ascii_binary_text[j].charAt(k) == (int)ascii_binary_key[i].charAt(k)){
                        temp_char += "0";
                    }else{
                        temp_char += "1";
                    }
                }
                ascii_binary_text[j] = temp_char;
            }
        }
        String cipher = "";
        for(int i = 0; i < text_lenght; i++){
            cipher += ascii_binary_text[i];
        }

        return cipher;

    }

    public static String decryptWithSpn(String cipherText, String key){

        char chars;
        String rkey = "";

        for (int i=0; i<key.length(); i++)
        {
            chars= key.charAt(i);
            rkey = chars+rkey;
        }
        key = rkey;


        int text_lenght = cipherText.length()/8;
        int key_lenght = key.length();

        char[] chars_key = new char[key_lenght];
        chars_key = key.toCharArray();

        String[] ascii_binary_cipher_text = new String[text_lenght];
        String[] ascii_binary_key = new String[key_lenght];

        String[] plain_text = new String[text_lenght];

        int count = 0;
        int start = 0;
        for(int i = 7; i < cipherText.length(); i+=8){
            char[] ch = new char[8];
            cipherText.getChars(start, i+1, ch, 0);
            ascii_binary_cipher_text[count] = new String(ch);
            start = i+1;
            count += 1;
        }

        for(int i = 0; i < key_lenght; i++){
            int temp = (int)chars_key[i];
            String result = Integer.toBinaryString(temp);
            while(result.length() < 8){
                result = "0" + result;
            }
            ascii_binary_key[i] = result;
        }

        for(int i = 0; i < key_lenght; i++){
            for(int j = 0; j<text_lenght; j++){
                String temp_char = "";
                for(int k = 0; k<=7; k++){
                    if(ascii_binary_cipher_text[j].charAt(k) == ascii_binary_key[i].charAt(k)){
                        temp_char += "0";
                    }else{
                        temp_char += "1";
                    }
                }
                ascii_binary_cipher_text[j] = temp_char;
            }
        }

        String plain = "";

        for(int i = 0; i < text_lenght; i++){
            int ascii_at_cipher = Integer.parseInt(ascii_binary_cipher_text[i],2);
            char char_at_cipher = (char)ascii_at_cipher;
            plain += char_at_cipher;
        }
        return plain;
    }
}
