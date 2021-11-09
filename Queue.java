package model;

public class Queue implements Runnable {

    private Thread th;
    private String status = "closed";
    private Client client;
    private int id;
    private String nameQueue;
    private int currentSec = 0; //secunda in momentul rularii
    private static int totalTime; //timpul de simulare

    public Queue(int id, String nameQueue) {
        this.id = id;
        this.nameQueue = nameQueue;
    }

    public void run() {
        if (currentSec <= totalTime) {
            while (currentSec <= totalTime) {

                try {
                    ++currentSec;
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (status != "closed") {
                    serveClient();
                }
            }}
        else
        {Thread.currentThread().interrupt();}
    }

    public void start() {

        if (th == null) {
            th = new Thread(this, nameQueue);
            th.start();
        }

    }

    private void serveClient() {

        int tservice = client.getTservice();
        if (tservice == 1) {
            removeClient();
        } else {
            tservice--;
            client.setTservice(tservice);

        }

    }

    public void addClient(Client client) {

        this.client = client;
        status = "busy";

    }

    private void removeClient() {

        status = "closed";
        //Thread.currentThread().interrupt();

    }

    public int getId() {

        return id;

    }

    public String getNameQueue() {

        return nameQueue;

    }

    public int getCurrentSec() {

        return currentSec;

    }

    public String getStatus() {

        return status;

    }

    public Client getClient() {

        return client;

    }
    public static int getTotalTime() {
        return totalTime;
    }
    public static void setTotalTime(int totalTime) {
        Queue.totalTime = totalTime;
    }
}
