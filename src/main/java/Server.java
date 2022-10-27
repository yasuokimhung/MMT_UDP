
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Administrator
 */
public class Server extends javax.swing.JFrame {

    private int port = 20008;
    private InetAddress clientIP;
    private int clientPort;
    public static Map<DatagramPacket, Integer> listSK;

    public Server(int port) {
        this.port = port;
    }

    public Server() {
        initComponents();
        this.setTitle("Server");
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        Server.listSK = new HashMap<DatagramPacket, Integer>();
        try {
            System.out.println("Starting........");
            areaServer.append("Nguyen Kim Hung giua ki UPD" + "\n");
            areaServer.append("Server start at port: " + port + "\n");
            DatagramSocket server = new DatagramSocket(port);
            WriteServer write = new WriteServer(server);
            write.start();
            // Đá Client ra khỏi Room nếu qua 10s
            KickClient kick = new KickClient();
            kick.start();

            ReceiveLoopFunction receiveLoopFunction = new ReceiveLoopFunction(server);
            receiveLoopFunction.start();

        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    class ReceiveLoopFunction extends Thread {

        private DatagramSocket server;

        public ReceiveLoopFunction(DatagramSocket server) {
            this.server = server;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //                TimeUnit.SECONDS.sleep(1);
                    String sms = recieveData(server);
                    for (DatagramPacket item : listSK.keySet()) {
                        if (!(item.getAddress().equals(clientIP) && item.getPort() == clientPort)) {
                            sendData(sms, server, item.getAddress(), item.getPort());
                        }
                    }
                    System.out.println(sms);
                    areaServer.append(sms + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private String recieveData(DatagramSocket server) throws IOException {
        byte[] temp = new byte[1024];
        DatagramPacket recieve_Packet = new DatagramPacket(temp, temp.length);
        server.receive(recieve_Packet);
        clientIP = recieve_Packet.getAddress();
        clientPort = recieve_Packet.getPort();
        checkDuplicate(recieve_Packet); // Kiểm tra trùng Packet trong mảng Map
        return new String(recieve_Packet.getData()).trim();
    }

    private void checkDuplicate(DatagramPacket packet) {
        for (DatagramPacket item : listSK.keySet()) {
            if (item.getAddress().equals(packet.getAddress()) && item.getPort() == packet.getPort()) {
                listSK.replace(item, 0);
                return; // Tìm thấy trùng thoát ra ngay
            }
        }
        listSK.put(packet, 0);
    }

    private void sendData(String value, DatagramSocket server, InetAddress clientIP, int clientPort)
            throws IOException {
        byte[] temp = new byte[1024];
        temp = value.getBytes();
        DatagramPacket send_result_Packet = new DatagramPacket(temp, temp.length, clientIP, clientPort);
        server.send(send_result_Packet);
    }

    class WriteServer extends Thread {

        private DatagramSocket server;

        public WriteServer(DatagramSocket server) {
            this.server = server;
        }

        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String sms = sc.nextLine();
                try {
                    for (DatagramPacket item : Server.listSK.keySet()) {
                        sendData("Server: " + sms, server, item.getAddress(), item.getPort());
                    }
                } catch (Exception e) {
                }
            }
        }

        private void sendData(String value, DatagramSocket server, InetAddress clientIP, int clientPort)
                throws IOException {
            byte[] temp = new byte[1024];
            temp = value.getBytes();
            DatagramPacket send_result_Packet = new DatagramPacket(temp, temp.length, clientIP, clientPort);
            server.send(send_result_Packet);
        }
    }

    class KickClient extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    for (Map.Entry<DatagramPacket, Integer> item : Server.listSK.entrySet()) {
                        int timeExist = item.getValue();
                        if (timeExist > 15000) {
                            Server.listSK.remove(item.getKey());
                            System.out.println("Da ngat ket noi " + item.getKey());
                            areaServer.append("Da ngat ket noi " + item.getKey() + "\n");
                        } else {
                            Server.listSK.replace(item.getKey(), timeExist + 1);
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    //To do something
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaServer = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel1.setText("Server");

        areaServer.setColumns(20);
        areaServer.setRows(5);
        jScrollPane1.setViewportView(areaServer);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(222, 222, 222)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
