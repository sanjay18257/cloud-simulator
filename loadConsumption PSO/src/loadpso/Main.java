package loadpso;

import javax.swing.*;
import java.awt.*;
import com.itextpdf.text.Document;
//import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.BaseColor;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import javax.swing.text.Document;

public class Main {
    private static boolean metricsDisplayed = false;
    private static JFrame predictionFrame = null;
    private static JFrame chartFrame = null;
    private static JFrame createHostFrame = null;
    private static JFrame qnpsoFrame = null;
    private static JFrame comparisonFrame = null;
    private static JFrame metricsFrame = null;
    
    
    public static void main(String[] args) {
    Login login = new Login();
    login.setVisible(true);
}


   public static void launchMainFrame(String username) {
    VMAllocation vm = new VMAllocation();
    vm.readVM();
    vm.readHost();
    
    for (int i = 0; i < 10; i++) {
        vm.createHost(1024, 4, 100);
    }
    vm.optimiseVmAllocation();

    JFrame frame = new JFrame("Cloud Resource Management System - User: " + username);
    frame.setSize(1200, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout(10, 10));
    
    // Add username label at top
    JLabel userLabel = new JLabel("Logged in as: " + username);
    userLabel.setFont(new Font("Arial", Font.BOLD, 14));
    userLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    frame.add(userLabel, BorderLayout.NORTH);

    JMenuBar menuBar = createMenuBar();
    frame.setJMenuBar(menuBar);
    
    JPanel mainPanel = createMainPanel();
    JTextArea hostDisplay = createHostDisplay();
    JScrollPane scrollPane = new JScrollPane(hostDisplay);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Host Status"));
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    
    JPanel controlPanel = createControlPanel(vm, hostDisplay);
    mainPanel.add(controlPanel, BorderLayout.NORTH);
    
    JPanel statusBar = createStatusBar();
    mainPanel.add(statusBar, BorderLayout.SOUTH);

    frame.add(mainPanel, BorderLayout.CENTER);
    updateHostDisplay(hostDisplay, vm);
    frame.setVisible(true);
    
    openCreateHostWindow(vm, hostDisplay);
}
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu toolsMenu = new JMenu("Tools");
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        return menuBar;
    }

    private static JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return mainPanel;
    }

    private static JTextArea createHostDisplay() {
        JTextArea hostDisplay = new JTextArea();
        hostDisplay.setEditable(false);
        hostDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        return hostDisplay;
    }

    private static JPanel createControlPanel(VMAllocation vm, JTextArea hostDisplay) {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        JButton requestVMButton = createStyledButton("Request VM", "request.png");
        JButton openChartButton = createStyledButton("PSO Metrics", "chart.png");
        JButton predictButton = createStyledButton("Load Prediction", "predict.png");
        JButton evaluateButton = createStyledButton("Evaluate Model", "evaluate.png");
        JButton qnpsoButton = createStyledButton("Run QNPSO", "qnpso.png");
        JButton compareButton = createStyledButton("Compare PSO/QNPSO", "compare.png");
compareButton.addActionListener(e -> showComparison());
controlPanel.add(compareButton);


        requestVMButton.addActionListener(e -> requestVM(vm, hostDisplay, null));
        openChartButton.addActionListener(e -> openChartFrame(vm));
        predictButton.addActionListener(e -> openPredictionFrame());
        evaluateButton.addActionListener(e -> handleEvaluation(vm));
        qnpsoButton.addActionListener(e -> runQNPSO(vm, hostDisplay));

        controlPanel.add(requestVMButton);
        controlPanel.add(openChartButton);
        controlPanel.add(predictButton);
        controlPanel.add(evaluateButton);
        controlPanel.add(qnpsoButton);

        return controlPanel;
    }

    private static JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("System Ready");
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }
  private static void showComparison() {
    if (comparisonFrame == null || !comparisonFrame.isDisplayable()) {
        comparisonFrame = new ComparisonResultsFrame();
        comparisonFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                comparisonFrame = null;
            }
        });
        comparisonFrame.setVisible(true);
    } else {
        comparisonFrame.setState(JFrame.NORMAL);
        comparisonFrame.toFront();
        comparisonFrame.requestFocus();
    }
}


private static void addLoadType(JPanel panel, String label, int value, Color color) {
    JPanel loadTypePanel = new JPanel();
    loadTypePanel.setLayout(new BoxLayout(loadTypePanel, BoxLayout.Y_AXIS));
    loadTypePanel.setBackground(Color.WHITE);
    loadTypePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

   
    JLabel titleLabel = new JLabel(label);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    
    JProgressBar progressBar = new JProgressBar(0, 150);
    progressBar.setValue(value);
    progressBar.setStringPainted(true);
    progressBar.setString(value + "%");
    progressBar.setPreferredSize(new Dimension(250, 20));
    progressBar.setBackground(Color.WHITE);
    progressBar.setForeground(color);
    progressBar.setBorderPainted(false);

    
    JLabel valueLabel = new JLabel(getLoadCategory(value));
    valueLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    valueLabel.setForeground(color);
    valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    loadTypePanel.add(titleLabel);
    loadTypePanel.add(Box.createVerticalStrut(5));
    loadTypePanel.add(progressBar);
    loadTypePanel.add(Box.createVerticalStrut(2));
    loadTypePanel.add(valueLabel);

    panel.add(loadTypePanel);
}

private static String getLoadCategory(int value) {
    if (value > 100) return "High Load";
    if (value > 50) return "Medium Load";
    return "Low Load";
}
  private static void runQNPSO(VMAllocation vm, JTextArea hostDisplay) {
    if (qnpsoFrame == null || !qnpsoFrame.isDisplayable()) {
        qnpsoFrame = new JFrame("QNPSO Optimization Results");
        qnpsoFrame.setSize(1000, 800);
        qnpsoFrame.setLayout(new BorderLayout());

        
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        
        outputArea.append("Starting QNPSO optimization...\n\n");

        
        PrintStream printStream = new PrintStream(new CustomOutputStream(outputArea));
        PrintStream oldOut = System.out;
        System.setOut(printStream);

        
        new Thread(() -> {
            try {
                int numUser = 1;
                Calendar calendar = Calendar.getInstance();
                boolean traceFlag = false;
                CloudSim.init(numUser, calendar, traceFlag);
                
                
                SwingUtilities.invokeLater(() -> 
                    outputArea.append("Initializing CloudSim environment...\n"));

                Datacenter datacenter = MainQnpso.createDatacenter("Datacenter_0");
                DatacenterBroker broker = MainQnpso.createBroker();
                MainQnpso.brokerId = broker.getId();

                SwingUtilities.invokeLater(() -> 
                    outputArea.append("Creating VMs and Cloudlets...\n"));

                List<Vm> vmList = MainQnpso.createVMs();
                List<Cloudlet> cloudletList = MainQnpso.createCloudlets();

                broker.submitVmList(vmList);
                broker.submitCloudletList(cloudletList);

                
                int numParticles = 50;
                int dimensions = vmList.size();
                int maxIterations = 100;

                SwingUtilities.invokeLater(() -> 
                    outputArea.append("\nStarting QNPSO optimization with:\n" +
                                    "Particles: " + numParticles + "\n" +
                                    "Dimensions: " + dimensions + "\n" +
                                    "Max Iterations: " + maxIterations + "\n\n"));

                QNPSO qnpso = new QNPSO(numParticles, dimensions, maxIterations);
                FitnessFunction fitnessFunction = new VMAllocationFitness(vmList, datacenter.getHostList());

                double[] optimizedAllocation = qnpso.optimize(fitnessFunction);

                SwingUtilities.invokeLater(() -> 
                    outputArea.append("Optimization completed. Applying results...\n\n"));

                QNPSOMonitor.printOptimizationResults(optimizedAllocation, vmList, datacenter.getHostList());
                MainQnpso.applyAllocation(optimizedAllocation, vmList, datacenter.getHostList());

                CloudSim.startSimulation();
                List<Cloudlet> newList = broker.getCloudletReceivedList();
                MainQnpso.printCloudletList(newList);
                CloudSim.stopSimulation();

                updateHostDisplay(hostDisplay, vm);
                System.setOut(oldOut);

                SwingUtilities.invokeLater(() -> 
                    outputArea.append("\nQNPSO optimization completed successfully!\n"));

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    outputArea.append("\nError during optimization: " + ex.getMessage() + "\n");
                    JOptionPane.showMessageDialog(qnpsoFrame, 
                        "Error running QNPSO: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

       
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = createStyledButton("Export Results", "export.png");
        JButton clearButton = createStyledButton("Clear Output", "clear.png");
        JButton closeButton = createStyledButton("Close", "close.png");

        exportButton.addActionListener(e -> exportToPDF(qnpsoFrame, "qnpso_results.pdf"));
        clearButton.addActionListener(e -> outputArea.setText(""));
        closeButton.addActionListener(e -> qnpsoFrame.dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(closeButton);

        qnpsoFrame.add(scrollPane, BorderLayout.CENTER);
        qnpsoFrame.add(buttonPanel, BorderLayout.SOUTH);

        qnpsoFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                qnpsoFrame = null;
            }
        });
        qnpsoFrame.setVisible(true);
    } else {
        qnpsoFrame.setState(JFrame.NORMAL);
        qnpsoFrame.toFront();
        qnpsoFrame.requestFocus();
    }
}

private static class CustomOutputStream extends OutputStream {
    private JTextArea textArea;
    
    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }
    
    @Override
    public void write(int b) {
        SwingUtilities.invokeLater(() -> textArea.append(String.valueOf((char)b)));
    }
}

  public static void showModelEvaluationMetrics(VMAllocation vm) {
    if (metricsFrame == null || !metricsFrame.isDisplayable()) {
        metricsFrame = new JFrame("Model Evaluation Dashboard");
        metricsFrame.setSize(1000, 700);
        metricsFrame.setLayout(new BorderLayout(15, 15));
        metricsFrame.getContentPane().setBackground(new Color(240, 240, 245));

        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel titleLabel = new JLabel("Model Performance Analytics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel mainContentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        mainContentPanel.setBackground(new Color(240, 240, 245));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            MLServerLoadPredictor predictor = new MLServerLoadPredictor(
                "C:/Users/sanja/OneDrive/Documents/holiday_calendar.csv");
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream oldOut = System.out;
            System.setOut(ps);
            predictor.evaluateModel();
            System.setOut(oldOut);
            String[] metrics = baos.toString().split("\n");
            
            for (String metric : metrics) {
                if (metric.contains(":")) {
                    String[] parts = metric.split(":");
                    mainContentPanel.add(createMetricCard(parts[0].trim(), parts[1].trim()));
                }
            }

            JPanel visualPanel = createVisualizationPanel();
            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            controlPanel.setBackground(new Color(240, 240, 245));
            
            JButton refreshButton = createStyledButton("Refresh Metrics", new Color(70, 130, 180));
            JButton exportButton = createStyledButton("Export Report", new Color(60, 179, 113));
            
            exportButton.addActionListener(e -> exportToPDF(metricsFrame, "model_metrics.pdf"));
            
            controlPanel.add(refreshButton);
            controlPanel.add(exportButton);

            metricsFrame.add(headerPanel, BorderLayout.NORTH);
            metricsFrame.add(mainContentPanel, BorderLayout.CENTER);
            metricsFrame.add(visualPanel, BorderLayout.EAST);
            metricsFrame.add(controlPanel, BorderLayout.SOUTH);

        } catch (IOException ex) {
            showErrorPanel(metricsFrame, "Error evaluating model: " + ex.getMessage());
        }

        metricsFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                metricsFrame = null;
                metricsDisplayed = false;
            }
        });
        metricsFrame.setVisible(true);
    } else {
        metricsFrame.setState(JFrame.NORMAL);
        metricsFrame.toFront();
        metricsFrame.requestFocus();
    }
}
private static JPanel createMetricCard(String label, String value) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));

    JLabel titleLabel = new JLabel(label);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    titleLabel.setForeground(new Color(70, 70, 70));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel valueLabel = new JLabel(value);
    valueLabel.setFont(new Font("Arial", Font.PLAIN, 24));
    valueLabel.setForeground(new Color(41, 128, 185));
    valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    card.add(titleLabel);
    card.add(Box.createVerticalStrut(10));
    card.add(valueLabel);

    return card;
}

private static JPanel createVisualizationPanel() {
    JPanel visualPanel = new JPanel(new BorderLayout());
    visualPanel.setPreferredSize(new Dimension(300, 0));
    visualPanel.setBackground(Color.WHITE);
    visualPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    
    
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(Color.WHITE);
    JLabel chartTitle = new JLabel("Load Distribution Analysis", SwingConstants.CENTER);
    chartTitle.setFont(new Font("Arial", Font.BOLD, 16));
    titlePanel.add(chartTitle);
    visualPanel.add(titlePanel, BorderLayout.NORTH);

    
    JPanel loadPanel = new JPanel();
    loadPanel.setLayout(new BoxLayout(loadPanel, BoxLayout.Y_AXIS));
    loadPanel.setBackground(Color.WHITE);

    
    addLoadType(loadPanel, "Weekend Load", 25, new Color(41, 128, 185));
    addLoadType(loadPanel, "Holiday Load", 76, new Color(46, 204, 113));
    addLoadType(loadPanel, "Festival Load", 126, new Color(230, 126, 34));
    addLoadType(loadPanel, "Regular Load", 0, new Color(52, 73, 94));

    JScrollPane scrollPane = new JScrollPane(loadPanel);
    scrollPane.setBorder(null);
    visualPanel.add(scrollPane, BorderLayout.CENTER);

    return visualPanel;
}

private static JButton createStyledButton(String text, Color backgroundColor) {
    JButton button = new JButton(text);
    button.setFont(new Font("Arial", Font.BOLD, 12));
    button.setForeground(Color.WHITE);
    button.setBackground(backgroundColor);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    return button;
}

private static void showErrorPanel(JFrame frame, String message) {
    JPanel errorPanel = new JPanel(new BorderLayout(10, 10));
    errorPanel.setBackground(new Color(255, 235, 235));
    errorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
    errorLabel.setForeground(new Color(180, 0, 0));
    errorPanel.add(errorLabel, BorderLayout.CENTER);
    
    frame.add(errorPanel);
}
    private static JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        return button;
    }

    private static void handleEvaluation(VMAllocation vm) {
        if (!metricsDisplayed) {
            metricsDisplayed = true;
            showModelEvaluationMetrics(vm);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Metrics dashboard is already open",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

     private static void openPredictionFrame() {
    if (predictionFrame == null || !predictionFrame.isDisplayable()) {
        predictionFrame = new JFrame("Server Load Prediction Dashboard");
        predictionFrame.setSize(1000, 600);
        predictionFrame.setLayout(new BorderLayout(10, 10));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel tablePanel = createPredictionTablePanel();
        JPanel chartPanel = createPredictionChartPanel();
        
        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(chartPanel);
        splitPane.setDividerLocation(600);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export");
        controlPanel.add(refreshButton);
        controlPanel.add(exportButton);
        exportButton.addActionListener(e -> exportToPDF(predictionFrame, "load_predictions.pdf"));

        predictionFrame.add(splitPane, BorderLayout.CENTER);
        predictionFrame.add(controlPanel, BorderLayout.SOUTH);
        predictionFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                predictionFrame = null;
            }
        });
        predictionFrame.setVisible(true);
    } else {
        predictionFrame.setState(JFrame.NORMAL);
        predictionFrame.toFront();
        predictionFrame.requestFocus();
    }
}
private static JPanel createPredictionChartPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Load Visualization"),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    
    JPanel chartPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawLoadChart(g, getWidth(), getHeight());
        }
    };
    chartPanel.setBackground(Color.WHITE);

    
    JPanel dataPanel = new JPanel();
    dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
    dataPanel.setBackground(Color.WHITE);

    try {
        MLServerLoadPredictor predictor = new MLServerLoadPredictor(
            "C:/Users/sanja/OneDrive/Documents/holiday_calendar.csv");
        List<PredictionResult> predictions = predictor.predictNext15Days();

        for (PredictionResult result : predictions) {
            addLoadBar(dataPanel, result);
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(panel, "Error loading predictions");
    }

    
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setTopComponent(chartPanel);
    splitPane.setBottomComponent(new JScrollPane(dataPanel));
    splitPane.setDividerLocation(300);
    splitPane.setResizeWeight(0.5);

    panel.add(splitPane, BorderLayout.CENTER);
    return panel;
}

private static void drawLoadChart(Graphics g, int width, int height) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int padding = 40;
    int chartWidth = width - 2 * padding;
    int chartHeight = height - 2 * padding;

    
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawLine(padding, height - padding, width - padding, height - padding);
    g2d.drawLine(padding, padding, padding, height - padding);

    try {
        MLServerLoadPredictor predictor = new MLServerLoadPredictor(
            "C:/Users/sanja/OneDrive/Documents/holiday_calendar.csv");
        List<PredictionResult> predictions = predictor.predictNext15Days();

        
        int xStep = chartWidth / (predictions.size() - 1);
        int[] xPoints = new int[predictions.size()];
        int[] yPoints = new int[predictions.size()];

        for (int i = 0; i < predictions.size(); i++) {
            xPoints[i] = padding + i * xStep;
            double load = predictions.get(i).getPredictedIncrease();
            yPoints[i] = height - padding - (int)((load / 150.0) * chartHeight);
        }

        g2d.setColor(new Color(41, 128, 185));
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < xPoints.length - 1; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }

        // Draw points
        for (int i = 0; i < xPoints.length; i++) {
            g2d.setColor(new Color(41, 128, 185));
            g2d.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
        }

        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= 5; i++) {
            int value = i * 30;
            int y = height - padding - (i * chartHeight / 5);
            g2d.drawString(value + "%", padding - 30, y + 5);
            g2d.drawLine(padding - 5, y, padding, y);
        }

    } catch (IOException ex) {
        g2d.drawString("Error loading data", width/2 - 50, height/2);
    }
}

private static void addLoadBar(JPanel panel, PredictionResult result) {
    JPanel loadBar = new JPanel(new BorderLayout(5, 0));
    loadBar.setBackground(Color.WHITE);
    loadBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

    JProgressBar bar = new JProgressBar(0, 150);
    bar.setValue((int)result.getPredictedIncrease());
    bar.setStringPainted(true);
    bar.setString(String.format("%.1f%%", result.getPredictedIncrease()));
    bar.setForeground(getLoadColor(result.getPredictedIncrease()));

    JLabel dateLabel = new JLabel(result.getDate());
    dateLabel.setPreferredSize(new Dimension(80, 20));

    loadBar.add(dateLabel, BorderLayout.WEST);
    loadBar.add(bar, BorderLayout.CENTER);
    panel.add(loadBar);
}

private static Color getLoadColor(double load) {
    if (load > 100) return new Color(231, 76, 60);  
    if (load > 50) return new Color(241, 196, 15);  
    return new Color(46, 204, 113);                 
}

    private static JPanel createPredictionTablePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Prediction Data"));
    
    String[] columns = {"Date", "Day", "Holiday", "Type", "Load (%)", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);
    
    try {
        MLServerLoadPredictor predictor = new MLServerLoadPredictor(
            "C:/Users/sanja/OneDrive/Documents/holiday_calendar.csv");
        List<PredictionResult> predictions = predictor.predictNext15Days();
        
        for (PredictionResult result : predictions) {
            String holidayName = result.getHolidayName().equals("None") && 
                                (result.getDay().equals("Saturday") || result.getDay().equals("Sunday")) 
                                ? "Weekend Holiday" : result.getHolidayName();
            
            String type = result.getType().equals("Regular") && 
                         (result.getDay().equals("Saturday") || result.getDay().equals("Sunday")) 
                         ? "Weekend" : result.getType();
            
            model.addRow(new Object[]{
                result.getDate(),
                result.getDay(),
                holidayName,
                type,
                String.format("%.2f", result.getPredictedIncrease()),
                getLoadStatus(result.getPredictedIncrease())
            });
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(panel, "Error loading predictions");
    }
    
    panel.add(new JScrollPane(table));
    return panel;
}


private static void addPredictionLoadBar(JPanel panel, PredictionResult result) {
    JPanel loadTypePanel = new JPanel();
    loadTypePanel.setLayout(new BoxLayout(loadTypePanel, BoxLayout.Y_AXIS));
    loadTypePanel.setBackground(Color.WHITE);
    loadTypePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Date and Type Label
    JLabel dateLabel = new JLabel(result.getDate() + " (" + result.getDay() + ")");
    dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
    dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Custom progress bar
    JProgressBar progressBar = new JProgressBar(0, 150);
    progressBar.setValue((int)result.getPredictedIncrease());
    progressBar.setStringPainted(true);
    progressBar.setString(String.format("%.2f%%", result.getPredictedIncrease()));
    progressBar.setPreferredSize(new Dimension(250, 20));
    progressBar.setBackground(Color.WHITE);
    progressBar.setForeground(getLoadColor(result.getPredictedIncrease()));
    progressBar.setBorderPainted(false);

    // Type and Status Label
    String type = result.getType().equals("Regular") && 
                 (result.getDay().equals("Saturday") || result.getDay().equals("Sunday")) 
                 ? "Weekend" : result.getType();
    JLabel typeLabel = new JLabel(type + " - " + getLoadStatus(result.getPredictedIncrease()));
    typeLabel.setFont(new Font("Arial", Font.ITALIC, 11));
    typeLabel.setForeground(getLoadColor(result.getPredictedIncrease()));
    typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

    loadTypePanel.add(dateLabel);
    loadTypePanel.add(Box.createVerticalStrut(2));
    loadTypePanel.add(progressBar);
    loadTypePanel.add(Box.createVerticalStrut(2));
    loadTypePanel.add(typeLabel);

    panel.add(loadTypePanel);
    panel.add(Box.createVerticalStrut(5));
}

//private static Color getLoadColor(double load) {
//    if (load > 100) return new Color(230, 126, 34); // High - Orange
//    if (load > 50) return new Color(46, 204, 113);  // Medium - Green
//    return new Color(52, 152, 219);                 // Low - Blue
//}

private static String getLoadStatus(double load) {
    if (load > 150) return "High";
    if (load > 100) return "Medium";
    return "Low";
}


private static void openChartFrame(VMAllocation vm) {
    if (chartFrame == null || !chartFrame.isDisplayable()) {
        chartFrame = new JFrame("PSO Performance Metrics");
        chartFrame.setSize(800, 600);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        PSOChart chart = new PSOChart();
        vm.metricsCalculator.setChart(chart);
        chartFrame.add(chart.getChartPanel(), BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> vm.metricsCalculator.updateChart(chart));
        controlPanel.add(refreshButton);
        
        chartFrame.add(controlPanel, BorderLayout.SOUTH);
        
        chartFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chartFrame = null;
            }
        });
        
        chartFrame.setVisible(true);
    } else {
        chartFrame.setState(JFrame.NORMAL); 
        chartFrame.toFront(); 
        chartFrame.requestFocus();
    }
}

//    private static JPanel createPredictionChartPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createTitledBorder("Load Visualization"));
//        // Add chart implementation here
//        return panel;
//    }
private static void exportToPDF(JFrame sourceFrame, String fileName) {
    try {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4);
        String downloadPath = "C:/Users/sanja/Downloads/";
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(downloadPath + fileName));
        document.open();

        // Create title with proper iText Font
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(sourceFrame.getTitle(), titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Create timestamp with proper iText Font
        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.Paragraph timestamp = new com.itextpdf.text.Paragraph("Generated on: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()), normalFont);
        timestamp.setSpacingAfter(20);
        document.add(timestamp);

        if (sourceFrame.getTitle().contains("Prediction")) {
            exportPredictionData(document);
        } else if (sourceFrame.getTitle().contains("Evaluation")) {
            exportMetricsData(document);
        }

        document.close();
        
        JOptionPane.showMessageDialog(sourceFrame, 
            "PDF exported successfully to Downloads\nFile: " + fileName,
            "Export Complete",
            JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(sourceFrame, 
            "Error exporting PDF: " + e.getMessage(),
            "Export Error",
            JOptionPane.ERROR_MESSAGE);
    }
}


private static void exportPredictionData(Document document) throws Exception {
    MLServerLoadPredictor predictor = new MLServerLoadPredictor(
        "C:/Users/sanja/OneDrive/Documents/holiday_calendar.csv");
    List<PredictionResult> predictions = predictor.predictNext15Days();

    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100);
    table.setSpacingBefore(20f);

    // Add headers with iText Font
    String[] headers = {"Date", "Day", "Type", "Load (%)", "Status"};
    com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
    
    for (String header : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }

  
    for (PredictionResult result : predictions) {
        table.addCell(result.getDate());
        table.addCell(result.getDay());
        table.addCell(result.getType());
        table.addCell(String.format("%.2f", result.getPredictedIncrease()));
        table.addCell(getLoadStatus(result.getPredictedIncrease()));
    }

    document.add(table);
}

private static void exportMetricsData(Document document) throws Exception {
    MLServerLoadPredictor predictor = new MLServerLoadPredictor(
        "C:/Users/sanja/OneDrive/Documents/holiday_calendar.csv");
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    PrintStream oldOut = System.out;
    System.setOut(ps);
    predictor.evaluateModel();
    System.setOut(oldOut);

    
    com.itextpdf.text.Font metricsFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
    String[] metrics = baos.toString().split("\n");
    for (String metric : metrics) {
        if (metric.contains(":")) {
            Paragraph p = new Paragraph(metric, metricsFont);
            p.setSpacingAfter(10);
            document.add(p);
        }
    }
}

    private static void updateHostDisplay(JTextArea hostDisplay, VMAllocation vmAllocation) {
        StringBuilder hostInfo = new StringBuilder();
        for (PowerHost host : vmAllocation.dt.hostList) {
            int totalRam = host.getRamProvisioner().getRam();
            int availableRam = host.getRamProvisioner().getAvailableRam();
            int totalCpu = host.getPeList().size();
            int usedCpu = 0;  

            for (PowerVm vm : vmAllocation.dt.vmlist) {
                if (vm.getHost() != null && vm.getHost().getId() == host.getId()) {
                    usedCpu += vm.getNumberOfPes(); 
                }
            }

            int availableBw = (int) host.getBwProvisioner().getAvailableBw();

       
            hostInfo.append("Host ID: ").append(host.getId())
                    .append(" | Total RAM: ").append(totalRam).append("MB | Used RAM: ")
                    .append(totalRam - availableRam).append("MB | Available RAM: ").append(availableRam).append("MB\n");
            hostInfo.append("Total CPU: ").append(totalCpu).append(" cores | Used CPU: ").append(usedCpu)
                    .append(" cores | Available BW: ").append(availableBw).append("MB/s\n");

          
            hostInfo.append("Allocated VMs:\n");
            for (PowerVm vm : vmAllocation.dt.vmlist) {
                if (vm.getHost() != null && vm.getHost().getId() == host.getId()) {
                    hostInfo.append("  VM ID: ").append(vm.getId())
                            .append(" | CPU: ").append(vm.getNumberOfPes())
                            .append(" | RAM: ").append(vm.getRam()).append("MB | BW: ").append(vm.getBw()).append("MB/s\n");
                }
            }
            hostInfo.append("\n");
        }
        hostDisplay.setText(hostInfo.toString());
    }

    private static void requestVM(VMAllocation vm, JTextArea hostDisplay, PSOChart chart) {
        String ram = JOptionPane.showInputDialog("Enter the required RAM (in MB):");
        String cpu = JOptionPane.showInputDialog("Enter the required CPU (in cores):");
        String bandwidth = JOptionPane.showInputDialog("Enter the required bandwidth (in MB/s):");
        String priorityInput = JOptionPane.showInputDialog("Enter the priority (1, 2, or 3):");

        int priority = Integer.parseInt(priorityInput);
        boolean isAllocated = vm.requestVM(Integer.parseInt(ram), Integer.parseInt(cpu), Integer.parseInt(bandwidth), priority);

        if (isAllocated) {
            JOptionPane.showMessageDialog(null, "VM allocated successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Could not allocate VM. Creating a new host...");
            vm.createHost(Integer.parseInt(ram), Integer.parseInt(cpu), Integer.parseInt(bandwidth));
        }

        updateHostDisplay(hostDisplay, vm);
        vm.metricsCalculator.updateChart(chart); // Refresh chart in the separate frame
    }

    private static void openCreateHostWindow(VMAllocation vm, JTextArea hostDisplay) {
        JFrame createHostFrame = new JFrame("Create Host");
        createHostFrame.setSize(400, 300);
        createHostFrame.setLayout(new GridLayout(4, 2));

        JTextField ramField = new JTextField();
        JTextField cpuField = new JTextField();
        JTextField bwField = new JTextField();

        createHostFrame.add(new JLabel("RAM (MB):"));
        createHostFrame.add(ramField);
        createHostFrame.add(new JLabel("CPU (cores):"));
        createHostFrame.add(cpuField);
        createHostFrame.add(new JLabel("Bandwidth (MB/s):"));
        createHostFrame.add(bwField);

        JButton createHostButton = new JButton("Create Host");
        createHostButton.addActionListener(e -> {
            try {
                int ram = Integer.parseInt(ramField.getText());
                int cpu = Integer.parseInt(cpuField.getText());
                int bw = Integer.parseInt(bwField.getText());
                vm.createHost(ram, cpu, bw);
                JOptionPane.showMessageDialog(createHostFrame, "Host created successfully!");
                updateHostDisplay(hostDisplay, vm);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(createHostFrame, "Please enter valid numbers.");
            }
        });

        createHostFrame.add(createHostButton);
        createHostFrame.setVisible(true);
    }
}
