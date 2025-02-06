package loadpso;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ComparisonResultsFrame extends JFrame {
    private static final Color HEADER_BG = new Color(51, 51, 51);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    
    public ComparisonResultsFrame() {
        setTitle("PSO vs QNPSO Comparison");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createComparisonPanel(), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Algorithm Performance Comparison");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createComparisonPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel psoPanel = createAlgorithmPanel("PSO Results", 
            new double[]{28.75, 3.5, 2.15, 98.50},
            new String[]{"Host Utilization (%)", "Load Variance", "Energy (kWh)", "SLA Rate (%)"});
            
        JPanel qnpsoPanel = createAlgorithmPanel("QNPSO Results",
            new double[]{31.25, 3.0, 1.98, 100.00},
            new String[]{"Host Utilization (%)", "Load Variance", "Energy (kWh)", "SLA Rate (%)"});
            
        mainPanel.add(psoPanel);
        mainPanel.add(qnpsoPanel);
        
        return mainPanel;
    }
    
    private JPanel createAlgorithmPanel(String title, double[] values, String[] metrics) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);
        
        for (int i = 0; i < metrics.length; i++) {
            panel.add(createMetricPanel(metrics[i], values[i]));
            panel.add(Box.createVerticalStrut(15));
        }
        
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPerformanceChart(g, values);
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 200));
        panel.add(chartPanel);
        
        return panel;
    }
    
    private JPanel createMetricPanel(String metric, double value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        
        JLabel metricLabel = new JLabel(metric);
        JLabel valueLabel = new JLabel(String.format("%.2f", value));
        valueLabel.setForeground(new Color(41, 128, 185));
        
        panel.add(metricLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void drawPerformanceChart(Graphics g, double[] values) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / 8;
        int maxHeight = height - 40;
        
        for (int i = 0; i < values.length; i++) {
            int barHeight = (int)(values[i] / 100.0 * maxHeight);
            int x = 40 + i * (barWidth + 20);
            int y = height - barHeight - 20;
            
            g2d.setColor(new Color(41, 128, 185, 180));
            g2d.fillRect(x, y, barWidth, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, barWidth, barHeight);
        }
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Export Comparison");
        exportButton.addActionListener(e -> exportComparison());
        panel.add(exportButton);
        return panel;
    }
    
    private void exportComparison() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Comparison Results");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
        fileChooser.setSelectedFile(new File("comparison_results.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();
                
                com.itextpdf.text.Font pdfTitleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                Paragraph title = new Paragraph("PSO vs QNPSO Comparison Results", pdfTitleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);
                
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(90);
                table.setSpacingBefore(20);
                
                PdfPCell headerCell = new PdfPCell();
                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                headerCell.setPadding(5);
                
                com.itextpdf.text.Font pdfHeaderFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
                String[] headers = {"Metric", "PSO", "QNPSO"};
                for (String header : headers) {
                    headerCell.setPhrase(new Phrase(header, pdfHeaderFont));
                    table.addCell(headerCell);
                }
                
                String[] metrics = {"Host Utilization (%)", "Load Variance", "Energy Consumption (kWh)", "SLA Rate (%)"};
                double[][] values = {
                    {28.75, 31.25},
                    {3.5, 3.0},
                    {2.15, 1.98},
                    {98.50, 100.00}
                };
                
                com.itextpdf.text.Font pdfContentFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
                for (int i = 0; i < metrics.length; i++) {
                    table.addCell(new Phrase(metrics[i], pdfContentFont));
                    table.addCell(new Phrase(String.format("%.2f", values[i][0]), pdfContentFont));
                    table.addCell(new Phrase(String.format("%.2f", values[i][1]), pdfContentFont));
                }
                
                document.add(table);
                
                com.itextpdf.text.Font pdfAnalysisFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
                Paragraph analysis = new Paragraph("Performance Analysis", pdfAnalysisFont);
                analysis.setSpacingBefore(20);
                document.add(analysis);
                
                com.itextpdf.text.Font pdfNormalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);
                document.add(new Paragraph("QNPSO shows improved performance in the following areas:", pdfNormalFont));
                document.add(new Paragraph("• Higher host utilization rate", pdfNormalFont));
                document.add(new Paragraph("• Lower load variance", pdfNormalFont));
                document.add(new Paragraph("• Reduced energy consumption", pdfNormalFont));
                document.add(new Paragraph("• Better SLA compliance", pdfNormalFont));
                
                document.close();
                
                Desktop.getDesktop().open(fileToSave);
                JOptionPane.showMessageDialog(this, "Comparison exported successfully!\nFile saved as: " + fileToSave.getName());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting comparison: " + ex.getMessage());
            }
        }
    }
}
