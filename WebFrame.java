import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class WebFrame extends JFrame {

    private static List<URLer> URLs = new ArrayList<URLer>();
    private Worker Worker;

    private Container pane;
    private TableMod model;
    private JTable table;

    private JTextField inputNums;

    private JLabel runningThreadsLabel;
    private JLabel comletedThreadsLabel;
    private JLabel elapsedLabel;

    private JProgressBar progressBar;
    private JButton singleFetchButton;
    private JButton concurentFetchButton;
    private JButton stoper;


    public WebFrame() {

        pane = this.getContentPane();
        setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

        List<String> urlList = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream("links.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                urlList.add(line);
            }

            fstream.close();
            br.close();
        } catch (Exception e) {

        }
        for (String adress : urlList){
            URLs.add(new URLer(adress));
        }
        model = new TableMod(URLs);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600, 300));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        singleFetchButton = new JButton("Single Thread Fetch");
        concurentFetchButton = new JButton("Concurent Fetch");
        stoper = new JButton("Stop");
        inputNums = new JTextField(5);
        inputNums.setMaximumSize( new Dimension( 200, 24 ) );
        inputNums.setText("4");
        runningThreadsLabel = new JLabel("Running: " + "0");
        comletedThreadsLabel = new JLabel("finshed: " + "0");
        elapsedLabel = new JLabel("Elapsed: " + "0");
        progressBar = new JProgressBar(0, URLs.size());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        pane.add(scrollpane);
        panel.add(singleFetchButton);
        panel.add(concurentFetchButton);
        panel.add(stoper);
        pane.add(panel);
        pane.add(inputNums);
        pane.add(runningThreadsLabel);
        pane.add(comletedThreadsLabel);
        pane.add(elapsedLabel);
        pane.add(progressBar);
        addListners();

        pack();
        setVisible(true);
    }





    private void addListners() {
        singleFetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Worker = new Worker(1);
                Worker.start();
            }
        });

        concurentFetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int laun = 1;
                try {
                    laun =  Integer.parseInt(inputNums.getText());
                } catch (NumberFormatException e) {
                }
                Worker = new Worker(laun);
                Worker.start();
            }
        });

        stoper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (Worker !=null) {
                    Worker.interrupt();
                }
            }
        });

    }

    class Worker extends Thread{

        private int numThreads;
        private long starter;
        private List<WebWorker> workersList = new ArrayList<WebWorker>();


        private Thread timeRefresher;
        private AtomicInteger threadsIn = new AtomicInteger(0);
        private AtomicInteger threadsOut = new AtomicInteger(0);
        public Semaphore semaphore;

        public Worker(int numThreads) {
            this.numThreads = numThreads;
        }

        @Override
        public void run() {
            semaphore = new Semaphore(numThreads);
            starter = System.currentTimeMillis();
            threadsIn.incrementAndGet();
            refresher();

            for (URLer urlObj : URLs) {
                try {
                    semaphore.acquire();
                    WebWorker worker2 = new WebWorker(urlObj, this);
                    workersList.add(worker2);
                    worker2.start();
                } catch (InterruptedException e) {
                    destroy();
                    break;
                }
            }

            threadsIn.decrementAndGet();
        }

        public void startThread() {
            threadsIn.incrementAndGet();
        }

        public void stopThread() {
            threadsIn.decrementAndGet();
        }

        public void completeThread() {
            threadsOut.incrementAndGet();
        }

        public void updatere() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    model.fireTableDataChanged();
                }
            });
        }

        public void refresher() {
            timeRefresher = new Thread() {
                @Override
                public void run() {
                    while(threadsIn.get() > 0){
                        try {
                            sleep(35);
                        } catch (InterruptedException e) {
                            break;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                elapsedLabel.setText("Elapsed: " +
                                        (System.currentTimeMillis() - starter) + "ms");

                            }
                        });
                    }

                }
            };
            timeRefresher.start();
        }

        public void updateInf() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    runningThreadsLabel.setText("Running: " +
                            Integer.toString(threadsIn.get()));
                    progressBar.setValue(threadsOut.get());
                    comletedThreadsLabel.setText("finshed: " +
                            Integer.toString(threadsOut.get()));


                }
            });
        }

        public void destroy() {
            for (WebWorker webWorker : workersList) {
                webWorker.interrupt();
                updatere();
                updateInf();
            }
        }


    }


    public static void main(String[] args) {
        WebFrame frame = new WebFrame();
    }

    private class TableMod extends AbstractTableModel {

        private List<URLer> URLs;


        TableMod(List<URLer> URLs) {
            this.URLs = URLs;
        }

        private String[] colums = {"url", "status"};

        @Override
        public String getColumnName(int column) {
            return colums[column];
        }

        @Override
        public int getColumnCount() {
            return colums.length;
        }

        @Override
        public int getRowCount() {
            return URLs.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (row < 0 || row >= URLs.size()){
                return null;
            }
            URLer obj = URLs.get(row);
            if(column==0){
                return obj.getUrll();
            }else if (column == 1){
                return obj.getSt();
            }
            return null;
        }
    }

    static class URLer{
        private String urll;
        private String status;

        public URLer(String urll) {
            this.urll = urll;
        }

        public String getUrll() {
            return urll;
        }
        public String getSt() {
            return status;
        }

        public void setSt(String status) {

            this.status = status;
        }
    }


}

