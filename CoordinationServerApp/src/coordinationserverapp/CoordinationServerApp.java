package coordinationserverapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoordinationServerApp {

    public static void main(String[] args) throws IOException {
        ServerSocket ssc = new ServerSocket(5000);
        // ServerSocket ss = new ServerSocket(6000);
        System.out.println("Coordinacion corriendo en puerto 5000 (cliente) y 6000 (Almacenamiento)");
        while (true) {
            Socket sc = ssc.accept();
            System.out.println("Socket (cliente) conectado " + sc.toString());
            DataInputStream dataIn = new DataInputStream(sc.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(sc.getOutputStream());
            String request = dataIn.readUTF();
            String host = dataIn.readUTF();
            if (request.equalsIgnoreCase("uploading")) {
                Socket conectionToSS = new Socket(host, 6000);
                System.out.println("Socket (almacenamiento)conectado " + conectionToSS.toString());
                FileReceiver fr = new FileReceiver(sc, conectionToSS);
                fr.run();
            } else if (request.equalsIgnoreCase("downloading")) {
                Socket conectionToSS = new Socket("localhost", 6000);
                System.out.println("Socket (almacenamiento) conectado " + conectionToSS.toString());
                FileSender fs = new FileSender(sc, conectionToSS);
                fs.run();
            } else if (request.equalsIgnoreCase("loadFiles")) {
                Socket conectionToSS = new Socket("localhost", 6000);
                System.out.println("Socket (almacenamiento) conectado " + conectionToSS.toString());
                FilesSender fss = new FilesSender(sc, conectionToSS);
                fss.run();
            } else if (request.equalsIgnoreCase("usercreation")) {
                Socket conectionToSS = new Socket("localhost", 6000);
                System.out.println("Socket (almacenamiento) conectado " + conectionToSS.toString());
                String name = dataIn.readUTF();
                String pass = dataIn.readUTF();
                String ubi = dataIn.readUTF();
                int idServer = dataIn.readInt();
                System.out.println("Datos received from client: name=" + name + ", pass=" + pass + ", ubi=" + ubi + ", idServer=" + idServer);
                Conexion c = new Conexion();
                c.getConexion();
                boolean resp = c.crearUser(name, pass, ubi, idServer);
                String x = "false";
                if (resp) {
                    x = "true";
                }
                dataOut.writeUTF(x);
            } else if (request.equalsIgnoreCase("uservalidation")) {
                Socket conectionToSS = new Socket("localhost", 6000);
                System.out.println("Socket (almacenamiento) conectado " + conectionToSS.toString());
                String name = dataIn.readUTF();
                String pass = dataIn.readUTF();
                System.out.println("Datos received from client: name=" + name + ", pass=" + pass);
                Conexion c = new Conexion();
                c.getConexion();
                String resp = c.login(name, pass);
                dataOut.writeUTF(resp);
            }else if (request.equalsIgnoreCase("ubicacion")) {
                Socket conectionToSS = new Socket("localhost", 6000);
                System.out.println("Socket (almacenamiento) conectado " + conectionToSS.toString());
                String name = dataIn.readUTF();
                System.out.println("Datos received from client: name=" + name);
                Conexion c = new Conexion();
                c.getConexion();
                String resp = c.ubicacion(name)+"";
                dataOut.writeUTF(resp);
            }
        }
    }

}

class FileReceiver implements Runnable {

    private final Socket clientSocket;
    private final Socket serverSocket;

    public FileReceiver(Socket clientSocket, Socket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(serverSocket.getOutputStream());
            String username = in.readUTF();
            String filename = in.readUTF();
            String path = in.readUTF();
            System.out.println("Receiving file: " + filename + " from " + username + " path " + path);

            long fileSize = in.readLong();
            System.out.println("File size: " + fileSize + " bytes");

            //Datos base de datos
            String fechaHoraIngreso = "", tipoArchivo = "";
            long tamanoBytes;
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            fechaHoraIngreso = ahora.format(formato);
            Conexion c =new Conexion();
            
            c.getConexion();
            
            //FileOutputStream out = new FileOutputStream(filename);
            byte[] buffer = new byte[1024];
            int read;
            long totalRead = 0;
            out.writeUTF("uploading");
            out.flush();
            out.writeUTF(username);
            out.flush();
            out.writeUTF(filename);
            out.flush();
            out.writeLong(fileSize);
            out.flush();
            out.writeUTF(path);
            out.flush();
            
            String auxx=filename.substring(filename.lastIndexOf(".") + 1);;
            while ((read = in.read(buffer, 0, Math.min(buffer.length, (int) (fileSize - totalRead)))) > 0) {
                totalRead += read;
                out.write(buffer, 0, read);
                c.Insertar(username, filename, fechaHoraIngreso, fileSize, auxx, path, "./"+username);
            }

            System.out.println("File received");

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }

//        Conexion c = new Conexion();
//        c.getConexion();
//        tipoArchivo = URLConnection.guessContentTypeFromName(f.getName());
//        c.Insertar(user, fileName, fechaHoraIngreso, tamanoBytes, tipoArchivo, f.getAbsolutePath(), "./" + user);
//        terminan db
}

class FileSender implements Runnable {

    private final Socket clientSocket;
    private final Socket serverSocket;

    public FileSender(Socket clientSocket, Socket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(serverSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(serverSocket.getOutputStream());
            DataInputStream clientIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());

            String username = clientIn.readUTF();
            String filename = clientIn.readUTF();
            System.out.println("Searching file: " + filename + " from server and sending it to " + username);

            out.writeUTF("downloading");
            out.writeUTF(username);
            out.writeUTF(filename);

            String name = in.readUTF();
            System.out.println("///>" + name);
            System.out.println("");
            long fileSize = in.readLong();
            int count = 0;
            System.out.println("File size: " + fileSize + " bytes");

            clientOut.writeUTF(name);
            clientOut.writeLong(fileSize);
            System.out.println("File data recieved from server");

            System.out.println("Aqui");

            byte[] buffer = new byte[1024];
            int read;

            while ((read = in.read(buffer)) > 0) {
                clientOut.write(buffer, 0, read);
                System.out.println("Transfering...");
            }

            System.out.println("File sent to client server");

        } catch (IOException ex) {
        }
    }
}

class FilesSender implements Runnable {

    private final Socket clientSocket;
    private final Socket serverSocket;

    public FilesSender(Socket clientSocket, Socket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(serverSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(serverSocket.getOutputStream());
            DataInputStream clientIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());

            String username = clientIn.readUTF();
            out.writeUTF("loadFiles");
            out.writeUTF(username);
            int totalFiles = in.readInt();
            clientOut.writeInt(totalFiles);

            for (int i = 0; i < totalFiles; i++) {
                clientOut.writeUTF(in.readUTF());
                clientOut.writeLong(in.readLong());
            }

        } catch (IOException ex) {
        }
    }
}
