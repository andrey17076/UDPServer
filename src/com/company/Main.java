package com.company;

import java.io.*;
import java.net.*;
import java.time.*;

public class Main {
    public static void main(String[] args) throws IOException {
        new UDPServer().start();
    }
}

class UDPServer extends Thread {

    protected byte[] buffer;
    protected DatagramSocket socket;
    protected InetAddress broadcastAddress;
    protected DatagramPacket packet;

    private static final int SELF_OPT = 0;
    private static final int BROADCASTING_OPT = 1;
    private static final int BUFFER_SIZE = 256;
    private static final int PORT = 5555;

    public UDPServer() {
        super("UDP SERVER THREAD");

        buffer = new byte[BUFFER_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);

        try {
            broadcastAddress = InetAddress.getByName("10.211.55.255");
        } catch (UnknownHostException e) {
            System.err.println(e);
        }

        try {
            socket = new DatagramSocket(PORT);
            socket.setBroadcast(true);
        } catch (SocketException e) {
            System.err.println(e);
        }
    }

    protected void sendPacket(InetAddress address) {
        buffer = LocalTime.now().toString().getBytes();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);

        try {
            socket.send(packet);
            System.out.println("Successful sent to " + packet.getAddress().getHostAddress() + "\n===================");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void run() {
        System.out.println("Waiting for request...\n");
        while (true) {
            try {
                socket.receive(packet);
                System.out.println("Received packet from " + packet.getAddress().getHostAddress()+"\n");

                if (packet.getData()[0] == BROADCASTING_OPT) {
                    System.out.println("Trying to broadcast time-info\n");
                    sendPacket(broadcastAddress);
                } else if (packet.getData()[0] == SELF_OPT) {
                    InetAddress address = packet.getAddress();
                    System.out.println("Trying to send back time-info\n");
                    sendPacket(address);
                } else {
                    String data = new String(packet.getData());
                    System.out.println("Received data: " + data + "\n===================");
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
