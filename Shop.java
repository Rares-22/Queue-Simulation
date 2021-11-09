package controller;

import model.Client;
import model.Queue;

import java.io.*;
import java.util.*;


public class Shop {
    private static List<Client> customers = new ArrayList<Client>();
    private static List<model.Queue> queues = new ArrayList<model.Queue>();
    private static int time = 0; // secunda curenta a rularii
    private static int nrQueue;
    private static int nrClient;
    private static int minArrival, maxArrival, minService, maxService;
    private static FileWriter output;
    private static double waitingTime = 0; //timp mediu de asteptare
    private static double serviceTime = 0;
    private static boolean ok = true;

    public static void main(String[] args) throws Exception {

        File f = new File("input_test_3");
        readData(f);
        addCustomers();
        addQueues();
        output = new FileWriter("output_test_3");

        while (!customers.isEmpty()) {
            Client client = customers.get(0); // luam primul client din coada
            here: for (model.Queue q : queues) {
                if (client.getTarrival() < q.getCurrentSec()) {
                    if (q.getStatus() == "closed") {
                        q.addClient(client);
                        waitingTime = waitingTime + ((q.getCurrentSec() - 1) - client.getTarrival() + client.getTservice());
                        // Din momentul in care iese din coada, scadem timpul in care a ajuns si timpul de servire
                        serviceTime = serviceTime + client.getTservice();
                        customers.remove(client);
                        if (customers.isEmpty() || customers.get(0).getTarrival() != client.getTarrival()|| customers.get(0).getTarrival()>= q.getCurrentSec()) {
                            break;
                        } else {
                            client = customers.get(0);
                            continue here;
                        }
                    }
                }
            }
            writeDataInFile();
        }
        while (ok) { //Afisam in cazul in care au ramas clienti in coada
            ok = false;
            for (model.Queue q : queues) {
                if (q.getStatus() == "busy")
                    ok = true;
            }
            writeDataInFile();
        }
        writeData("\nAverage waiting time:" + (double) waitingTime / nrClient);
        writeData("\nAverage service time:" + (double) serviceTime / nrClient);
        output.close();
    }

    private static void addCustomers() {
        Random rand = new Random();
        for (int i = 0; i < nrClient; i++) {
            int randomA = rand.nextInt(maxArrival - minArrival + 1) + minArrival;
            int randomS = rand.nextInt(maxService - minService + 1) + minService;
            Client client = new Client(i + 1, randomA, randomS);
            customers.add(client);
        }
        Collections.sort(customers, new Comparator<Client>() {

            public int compare(Client client1, Client client2) {
                return (client1.getTarrival() - client2.getTarrival());
            }
        });
    }

    private static void addQueues() {

        for (int i = 1; i <= nrQueue; i++) {
            model.Queue queue = new model.Queue(i, "model.Queue" + i);
            queues.add(queue);
            queue.start();
        }
    }

    private static void printClients() throws IOException {
        writeData("Waiting clients: ");
        for (Client client : customers) {
            writeData("(" + client.getid() + "," + client.getTarrival() + "," + client.getTservice() + ");");
        }
        writeData("\n");
    }

    private static void printQueues() throws IOException {
        for (model.Queue queue : queues) {
            if (queue.getStatus() == "closed") {
                writeData("model.Queue" + queue.getId() + ": closed");
                writeData("\n");
            } else {
                writeData("model.Queue" + queue.getId() + ": (" + queue.getClient().getid() + "," + queue.getClient().getTarrival() + ","
                        + queue.getClient().getTservice() + ");");
                writeData("\n");
            }
        }
    }

    private static void readData(File fin) throws NumberFormatException, IOException {

        BufferedReader buffer = new BufferedReader(new FileReader(fin));
        String sequance; // pentru intervale
        nrClient = Integer.parseInt(buffer.readLine());
        nrQueue = Integer.parseInt(buffer.readLine());
        int totalTime = Integer.parseInt(buffer.readLine());
        Queue.setTotalTime(totalTime);
        sequance = buffer.readLine();
        String[] sir = sequance.split(",");
        minArrival = Integer.parseInt(sir[0]);
        maxArrival = Integer.parseInt(sir[1]);
        sequance = buffer.readLine();
        sir = sequance.split(",");
        minService = Integer.parseInt(sir[0]);
        maxService = Integer.parseInt(sir[1]);
    }

    public static void writeData(String dataToWrite) throws IOException {
        try {
            output.write(dataToWrite);
        } catch (Exception e) {
            System.out.println("Exception");
        }

    }

    public static void writeDataInFile() throws IOException {
        writeData("\nTime " + time++ + "\n");
        printClients();
        printQueues();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }



}
