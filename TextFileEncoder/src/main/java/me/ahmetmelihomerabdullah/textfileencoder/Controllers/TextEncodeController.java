package me.ahmetmelihomerabdullah.textfileencoder.Controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import me.ahmetmelihomerabdullah.textfileencoder.Server.Server;
import me.ahmetmelihomerabdullah.textfileencoder.Utils.FileEncryption;
import me.ahmetmelihomerabdullah.textfileencoder.Utils.FileZIP;
import me.ahmetmelihomerabdullah.textfileencoder.Utils.SHA256;
import me.ahmetmelihomerabdullah.textfileencoder.Utils.SPN;

public class TextEncodeController implements Initializable {
    @FXML
    private TextField selectedKeyField;

    @FXML
    private TextArea textInput;

    @FXML
    private TextArea keyTextArea;

    @FXML
    private TextArea plainTextArea;

    @FXML
    private TextArea cipherTextArea;

    @FXML
    private TextField selectedFilePath;

    @FXML
    private CheckBox sha256CheckBox;

    @FXML
    private CheckBox spnCheckBox;

    @FXML
    private Button sendButton;

    @FXML
    private TextField fileExtension;

    @FXML
    private ImageView textEncryptionImageView;

    @FXML
    private ImageView fileEncryptionImageView;

    @FXML
    private ImageView serverStatusImageView;

    @FXML
    private ImageView connectionStatusImageView;

    @FXML
    private Label secretKey;

    public static TextArea originPlainTextArea;
    public static TextArea originCipherTextArea;
    public static TextField originFileExtension;

    static Alert alert = new Alert(Alert.AlertType.ERROR);
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static int keyStat = 0;
    public static String key = null;
    public static String sha256CipherText = null;
    public static String sha256PlainText = null;
    public static String spnCipherText = null;

    public static File selectedFile = null;
    public static File zippedFile = null;

    public boolean connectionStatus = false;
    public String userName = null;

    private boolean fileEncodeStat = false;
    private boolean textEncodeStat = false;
    public static boolean sha256EncodeStat = false;
    public static boolean spnEncodeStat = false;

    private Thread startServerThread, stopServerThread;
    public static Socket socket;
    private static DataInputStream din;
    private static DataOutputStream dout;
    private Server server;

    private Image FirstTick = new Image(getClass().getResourceAsStream("/images/FirstTick.png"));
    private Image SecTick = new Image(getClass().getResourceAsStream("/images/SecTick.png"));

    public void initialize(URL location, ResourceBundle resources) {
        this.originPlainTextArea = plainTextArea;
        this.originCipherTextArea = cipherTextArea;
        this.originFileExtension = fileExtension;

        textEncryptionImageView.setImage(SecTick);
        fileEncryptionImageView.setImage(SecTick);
        connectionStatusImageView.setImage(SecTick);

        checkServerStatus();
        checkConnectionStatus();
    }

    public void showKey() {
        if(key != null) {
            if(keyStat == 0) {
                selectedKeyField.setText(key);
                keyStat = 1;
            } else if(keyStat == 1) {
                selectedKeyField.setText("••••••••");
                keyStat = 0;
            }
        }
    }

    public void sha256CheckBoxChange() {
        if(!spnCheckBox.isSelected()) {
            if(sha256CheckBox.isSelected()) {
                System.out.println("SHA256 SEÇİLDİ!");
            }
        } else {
            clrChoices();
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Aynı anda sadece tek bir şifreleme algoritması seçebilirsin.");
            alert.showAndWait();
        }
    }

    public void spnCheckBoxChange() {
        if(!sha256CheckBox.isSelected()) {
            if(spnCheckBox.isSelected()) {
                System.out.println("SPN SEÇİLDİ!");
            }
        } else {
            clrChoices();
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Aynı anda sadece tek bir şifreleme algoritması seçebilirsin.");
            alert.showAndWait();
        }
    }

    public void encryptPlainText() throws Exception {
        if(!sha256CheckBox.isSelected() && !spnCheckBox.isSelected()) {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Programın temel mantığı zaten şifrelemek o yüzden lütfen yukarıdan bir tane şifreleme algoritması seç.");
            alert.showAndWait();
        } else {
            if(key != null) {
                if(sha256CheckBox.isSelected()) {
                    if(textInput.getText() != null) {
                        sha256CipherText = SHA256.hashMac(textInput.getText(), key);
                        sha256PlainText = plainTextArea.getText();
                        plainTextArea.setText(textInput.getText());
                        cipherTextArea.setText(sha256CipherText);
                        textEncryptionImageView.setImage(FirstTick);
                        textEncodeStat = true;
                        sha256EncodeStat = true;
                        textInput.setText(null);
                    }
                } else if(spnCheckBox.isSelected()) {
                    spnCipherText = SPNencrypt(textInput.getText(), key);
                    plainTextArea.setText(SPNDecrypt(spnCipherText, key));
                    cipherTextArea.setText(spnCipherText);
                    textEncryptionImageView.setImage(FirstTick);
                    textEncodeStat = true;
                    spnEncodeStat = true;
                    textInput.setText(null);
                }
            } else {
                alert.setTitle("HATA!");
                alert.setHeaderText("Şifreleme Algoritması Hatası.");
                alert.setContentText("Lütfen şifreleme anahtarı belirle. Aksi takdirde düzgün bir şifreleme elde edemezsin.");
                alert.showAndWait();
            }
        }
    }

    public void settingKey() {
        if(keyTextArea.getText() != null) {
            key = keyTextArea.getText();
            selectedKeyField.setText("••••••••");
            clrChoices();
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Lütfen şifreleme anahtarı gir. Aksi takdirde düzgün bir şifreleme elde edemezsin.");
            alert.showAndWait();
        }
    }

    public void clearEncodedText() {
        if(textEncodeStat) {
            plainTextArea.setText(null);
            cipherTextArea.setText(null);
            textEncryptionImageView.setImage(SecTick);
            textEncodeStat = false;
            spnEncodeStat = false;
            sha256EncodeStat = false;
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Temizleme Hatası.");
            alert.setContentText("Hiçbir metni şifrelemediğin için dosya bölümünü temizleyemezsin.");
            alert.showAndWait();
        }
    }

    public void clearEncodedFile() {
        if(fileEncodeStat) {
            fileEncryptionImageView.setImage(SecTick);
            fileEncodeStat = false;
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Temizleme Hatası.");
            alert.setContentText("Hiçbir dosyayı şifrelemediğin için dosya bölümünü temizleyemezsin.");
            alert.showAndWait();
        }
    }

    public void clearAllAction() {
        if(textEncodeStat || fileEncodeStat) {
            plainTextArea.setText(null);
            cipherTextArea.setText(null);
            clrChoices();
            textEncryptionImageView.setImage(SecTick);
            fileEncryptionImageView.setImage(SecTick);
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Temizleme Hatası.");
            alert.setContentText("Hiçliği temizleyemezsin.");
            alert.showAndWait();
        }
    }

    public void startServer() throws IOException {
        serverStartStop(1, isPortAvailable(1334));
    }

    public void stopServer() throws IOException {
        serverStartStop(0, isPortAvailable(1334));
    }

    public void fileEncodingButton() {
        FileChooser ds = new FileChooser();
        ds.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sadece .txt, .dat, .gif", "*.txt", "*.dat", "*.gif", "*.encrypted"));
        File d = ds.showOpenDialog(null);
        if(d != null) {
            selectedFile = d;
            selectedFilePath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void encodeSelectedFile() {
        if(selectedFile != null && key != null) {
            try {
                originFileExtension.setText(findExtension(selectedFile.getAbsolutePath()));
                FileEncryption.encryptFile(selectedFile.getAbsolutePath(), key);
                fileEncryptionImageView.setImage(FirstTick);
                fileEncodeStat = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Dosya şifrelemek için önce bir anahtar belirlemeli ve şifrelenecek dosyayı seçmelisin.");
            alert.showAndWait();
        }
    }

    public void connectionButton() throws IOException {
        if(!isPortAvailable(1334)) {
            connectServer(1);
            connectionStatus = true;
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Sunucu Hatası.");
            alert.setContentText("Açık olmayan sunucuya bağlanamazsın.");
            alert.showAndWait();
        }
    }

    public void sendButtonAction() throws IOException, InterruptedException {
        if(connectionStatus) {
            if(textEncodeStat) {
                if(sha256CipherText != null) {
                    try {
                        dout.writeUTF(cipherTextArea.getText());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else if(spnCipherText != null) {
                    try {
                        dout.writeUTF(cipherTextArea.getText() + "hidspn" + key);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            } else if(fileEncodeStat) {
                File encryptedAndCompressedFile = new File(selectedFile.getAbsoluteFile() + ".encrypted.zip");
                System.out.println(encryptedAndCompressedFile.getAbsolutePath());
                //client.sendFileName(encryptedAndCompressedFile.getAbsolutePath());
                //client.sendFile(encryptedAndCompressedFile.getAbsolutePath());
                clearEncodedFile();
            }
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Bağlantı Hatası.");
            alert.setContentText("Mesaj || dosya göndermek için önce sunucuya bağlanmalısın.");
            alert.showAndWait();
        }
    }

    public void decryptMessage() throws Exception {
        if(cipherTextArea.getText() != null) {
            String cipherTextT = cipherTextArea.getText();
            if(checkBinary(cipherTextT) == true) {
                if(key.equals(secretKey.getText())) {
                    plainTextArea.setText(SPNDecrypt(cipherTextT, key));
                } else {
                    alert.setTitle("HATA!");
                    alert.setHeaderText("Şifre Hatası.");
                    alert.setContentText("Anahtarın mesajı gönderen kullanıcı ile aynı değil.");
                    alert.showAndWait();
                }
            } else {
                plainTextArea.setText("Aldığın mesaj bir hash bu yüzden bunu çözemezsin.");
            }
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Metin Hatası.");
            alert.setContentText("Herhangi bir mesaj almamışsın.");
            alert.showAndWait();
        }
    }

    public void takeFile() {
        //client.listen4File();
    }

    //Gerekli fonksiyonlar:
    public static File decodeSelectedFile(File selectedDecFile) {
        if(selectedDecFile != null && key != null && originFileExtension.getText() != null) {
            try {
                if(selectedDecFile.getName().contains("encrypted")) {
                    Path source = Paths.get(selectedDecFile.getAbsolutePath());
                    Path target = Paths.get(System.getProperty("user.home") + "/Desktop");
                    FileZIP.unzipFolder(source, target);
                    File delete = new File(selectedDecFile.getAbsolutePath());
                    delete.delete();
                    return FileEncryption.decryptFile(selectedDecFile.getAbsolutePath(), key, originFileExtension.getText());
                } else {
                    alert.setTitle("HATA!");
                    alert.setHeaderText("Şifreleme Algoritması Hatası.");
                    alert.setContentText("Yalnızca şifrelenmiş dosyaları içeren arşivleri seçebilirsin.");
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            alert.setTitle("HATA!");
            alert.setHeaderText("Şifreleme Algoritması Hatası.");
            alert.setContentText("Dosya şifrelemek için önce bir anahtar belirlemeli ve şifrelenecek dosyayı seçmelisin.");
            alert.showAndWait();
        }
        return null;
    }

    private void clrChoices() {
        sha256CheckBox.setSelected(false);
        spnCheckBox.setSelected(false);
        textInput.setText(null);
        keyTextArea.setText(null);
        textEncodeStat = false;
        fileEncodeStat = false;
        sha256EncodeStat = false;
        spnEncodeStat = false;
    }

    private String SPNencrypt(String plainText, String key) throws Exception {
        String encryptedTextBase64 = SPN.encryptWithSpn(plainText, key);
        return encryptedTextBase64;
    }

    private String SPNDecrypt(String cipherText, String key) throws Exception {
        String decryptedTextBase64 = SPN.decryptWithSpn(cipherText, key);
        return decryptedTextBase64;
    }

    public static String findExtension(String fileName) {
        String extension = "";

        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }
        return "." + extension;
    }

    private static boolean isPortAvailable(int port) {
        try {
            ServerSocket srv = new ServerSocket(port);
            srv.close();
            srv = null;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void serverStartStop(int serverStatus, boolean portStatus) throws IOException {
        if(serverStatus == 1) {
            Task<Void> startServerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ServerSocket serverSocket = new ServerSocket(1334);
                    server = new Server(serverSocket);
                    Task<Void> startFunctionCallTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            server.startServer();
                            return null;
                        }
                    };
                    Thread startFunctionCallThread = new Thread(startFunctionCallTask);
                    startFunctionCallThread.start();
                    System.out.println("SUNUCU BASLATILDI !!");
                    return null;
                }
            };

            startServerThread = new Thread(startServerTask);
            if(portStatus == false) {
                alert.setTitle("HATA!");
                alert.setHeaderText("Server Hatası.");
                alert.setContentText("Açık olan sunucuyu tekrar açamazsın.");
                alert.showAndWait();
            } else if(portStatus == true) {
                startServerThread.start();
                serverStatusImageView.setImage(FirstTick);
            }
        } else if(serverStatus == 0) {
            Task<Void> stopServerTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    server.closeServerSocket();
                    return null;
                }
            };

            stopServerThread = new Thread(stopServerTask);
            if(portStatus == false) {
                stopServerThread.start();
                serverStatusImageView.setImage(SecTick);
            } else if(portStatus == true) {
                alert.setTitle("HATA!");
                alert.setHeaderText("Server Hatası.");
                alert.setContentText("Açık olmayan sunucuyu kapatamazsın.");
                alert.showAndWait();
            }
        }
    }

    private void checkServerStatus() {
        Runnable serverStatusImageRunnable = new Runnable() {
            public void run() {
                if(isPortAvailable(1334)) {
                    serverStatusImageView.setImage(SecTick);
                } else {
                    serverStatusImageView.setImage(FirstTick);
                }
            }
        };

        ScheduledExecutorService serverStatusExec = Executors.newScheduledThreadPool(1);
        serverStatusExec.scheduleAtFixedRate(serverStatusImageRunnable , 0, 1, TimeUnit.SECONDS);
    }

    private void checkConnectionStatus() {
        Runnable serverStatusImageRunnable = new Runnable() {
            public void run() {
                if(socket.isConnected()) {
                    connectionStatusImageView.setImage(FirstTick);
                } else {
                    connectionStatusImageView.setImage(SecTick);
                }
            }
        };

        ScheduledExecutorService serverStatusExec = Executors.newScheduledThreadPool(1);
        serverStatusExec.scheduleAtFixedRate(serverStatusImageRunnable , 0, 1, TimeUnit.SECONDS);
    }

    private void connectServer(int type) {
        if(type == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket("127.0.0.1", 1334);
                        connectionStatusImageView.setImage(FirstTick);
                        System.out.println("Sunucuyla bağlantı kuruldu.");
                        din = new DataInputStream(socket.getInputStream());
                        dout = new DataOutputStream(socket.getOutputStream());
                        String msgin = "";
                        while (!msgin.equals("exit")) {
                            msgin = din.readUTF();
                            if(msgin.split("hidspn") != null) {
                                cipherTextArea.setText(msgin.split("hidspn")[0]);
                                String finalMsgin = msgin;
                                Platform.runLater(new Runnable(){
                                    @Override
                                    public void run() {
                                        secretKey.setText(finalMsgin.split("hidspn")[1]);
                                    }
                                });
                            } else {
                                cipherTextArea.setText(msgin);
                            }
                        }

                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }).start();
        }
    }

    private boolean checkBinary(String cipherText) {
        if (cipherText.matches("[01]+")) {
            return true;
        }
        return false;
    }
}
