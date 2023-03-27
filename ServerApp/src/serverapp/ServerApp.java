package serverapp;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServerApp {
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        ServerSocket serverSocket = new ServerSocket(6000); // Replace 12345 with your desired port number
        System.out.println("Server corriendo en puerto 6000");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Socket conectado " + socket.toString());
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataIn = new DataInputStream(inputStream);
            String request = dataIn.readUTF();
            System.out.println("-" + request);
            if (request.equalsIgnoreCase("uploading")) {
                //File f = (File) ois.readObject();
                String username = dataIn.readUTF();
                receivingFile(socket, username);
            } else if (request.equalsIgnoreCase("downloading")) {
                String username = dataIn.readUTF();
                String filename = dataIn.readUTF();
                System.out.println("Se leyo el nombre de archivo " + filename + " en la capeta del usuario " + username);
                sendFile(socket, filename, username);
            } else if (request.equalsIgnoreCase("loadFiles")) {
                String username = dataIn.readUTF();
                sendAllFiles(socket, username);
                System.out.println("Leyendo todos los archivos bajo la carpeta del usuario " + username);
            }
            socket.close();
        }
        //serverSocket.close();
    }
    
    public static void sendFile(Socket socket, String fileName, String username) throws IOException {
        System.out.println("Connected to client");
        System.out.println("Sending file to client");
        // Send file to client 
        OutputStream out = socket.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        byte[] buffer = new byte[1024];
        int count;
        File fileToSend = new File("../ServerApp/" + username + "/" + fileName); // Replace with your desired file name
        dataOut.writeUTF(fileToSend.getName()); // Send file name to client
        dataOut.flush();
        dataOut.writeLong(fileToSend.length()); // Send file size to client
        dataOut.flush();
        FileInputStream fileIn = new FileInputStream(fileToSend);
        while ((count = fileIn.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        fileIn.close();
        System.out.println("File sent to coordination");
    }
    
    public static void receivingFile(Socket socket, String user) throws IOException {
        System.out.println("Connected to client");
        System.out.println("Receiving file from client");
        InputStream in = socket.getInputStream();

        //Datos base de datos
        String fechaHoraIngreso = "", tipoArchivo = "";
        long tamanoBytes;
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        fechaHoraIngreso = ahora.format(formato);

        // Receive file from client 
        byte[] buffer = new byte[1024];
        int count;
        DataInputStream dataIn = new DataInputStream(in);
        String fileName = dataIn.readUTF(); // Read file name from client
        File f = new File("./" + user);
        
        f.mkdir();
        
        FileOutputStream fileOut = new FileOutputStream("./" + user + "/" + fileName);
        long fileSize = dataIn.readLong(); // Read file size from client
        while (fileSize > 0 && (count = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
            fileOut.write(buffer, 0, count);
            fileSize -= count;
        }
        
        fileOut.close();
        System.out.println("File received from client");
        tamanoBytes = f.length();
        System.out.println("Archivo seleccionado: " + fileName + "\n"
                + "fecha de ingreso: " + fechaHoraIngreso + "\n"
                + "tamanoBytes: " + fileSize + "\n"
                + "tipo Archivo: " + tipoArchivo);
    }
    
    public static void sendAllFiles(Socket sc, String username) throws IOException, FileNotFoundException {
        
        File directory = new File("../ServerApp/" + username + "/");
        DataOutputStream out = new DataOutputStream(sc.getOutputStream());
        File[] dirFiles = directory.listFiles();
        int totalFiles = dirFiles.length;
        System.out.println(totalFiles);
        out.writeInt(totalFiles);
        
        for (File dirFile : dirFiles) {
            out.writeUTF(dirFile.getName());
            out.writeLong(dirFile.length());
        }
        
    }
}
