import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class Simulator{
    private JFrame frame;
    private JTable table;
    private DefaultTableModel defaultTableModel;
    private JTextArea resultString;
    private JTextArea avgString;

    public Simulator(String type, ArrayList<Process> procedure, ArrayList<String> result, Criteria criteria, ArrayList<Process> jobs){

        StringBuilder sb = new StringBuilder();

        for(int i=0 ; i<result.size() ; i++){
            sb.append(result.get(i));
            sb.append("\n");
        }
        String concat = sb.toString();

        sb = new StringBuilder();

        for(int i=0 ; i<jobs.size(); i++){
            sb.append("P" + jobs.get(i).PID + "\n");
            sb.append("Turnaround Time : " + jobs.get(i).turnaroundTime + "\n");
            sb.append("Waiting Time : " + jobs.get(i).waitTime + "\n");
            sb.append("Response Time : " + jobs.get(i).responseTime + "\n");
            sb.append("\n");
        }

        sb.append("============================\n");
        sb.append(type + "스케줄링\n");
        sb.append("총 시간 : " + procedure.get(procedure.size()-1).swapOut + "\n");
        sb.append("CPU 이룡률 : " + criteria.avgCPUU + "\n");
        sb.append("평균 Turnaround Time : " + criteria.avgTT + "\n");
        sb.append("평균 Waiting Time : " + criteria.avgWT + "\n");
        sb.append("평균 Response Time : " + criteria.avgRT + "\n");

        String avg = sb.toString();

        run(concat, avg, procedure);
    }


    private void run(String concat, String avg, ArrayList<Process> procedure){
        frame = new JFrame();
        frame.setBounds(50,50, 900,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel gcLabel = new JLabel("간트 차트");
        gcLabel.setBounds(15,10, 750, 15);
        frame.getContentPane().add(gcLabel);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(15, 30, 850, 100);
        frame.getContentPane().add(scrollPane);

        String column[] = new String[procedure.size()];

        for(int i=0;i< column.length; i++){
            column[i] = "P" + Integer.toString(procedure.get(i).PID);
        }

        String content[][] = new String[2][procedure.size()];

        for(int j=0; j<procedure.size(); j++){
            String swapIn = Integer.toString(procedure.get(j).swapIn);
            String swapOut = Integer.toString(procedure.get(j).swapOut);
            content[0][j] = swapIn + " - " + swapOut;
        }
        for(int j=0; j<procedure.size(); j++){
            int swapIn = procedure.get(j).swapIn;
            int swapOut = procedure.get(j).swapOut;
            content[1][j] = Integer.toString(swapOut-swapIn);
        }

        defaultTableModel = new DefaultTableModel(column, 0);
        defaultTableModel.addRow(content[0]);
        defaultTableModel.addRow(content[1]);

        table = new JTable(defaultTableModel);
        table.setEnabled(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        scrollPane.setViewportView(table);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for(int j=0; j<procedure.size(); j++){
            int swapIn = procedure.get(j).swapIn;
            int swapOut = procedure.get(j).swapOut;
            table.getColumnModel().getColumn(j).setPreferredWidth((swapOut - swapIn)*55);
        }

        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
        defaultTableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumnModel tableColumnModel = table.getColumnModel();

        for(int i=0;i< tableColumnModel.getColumnCount(); i++){
            tableColumnModel.getColumn(i).setCellRenderer(defaultTableCellRenderer);
        }


        JScrollPane avgPane = new JScrollPane();
        avgPane.setBounds(15, 180, 350, 350);
        frame.getContentPane().add(avgPane);

        avgString = new JTextArea();
        avgString.setText(avg);
        avgPane.setViewportView(avgString);

        JScrollPane resultPane = new JScrollPane();
        resultPane.setBounds(420, 180, 350, 350);
        frame.getContentPane().add(resultPane);

        resultString = new JTextArea();
        resultString.setText(concat);
        resultPane.setViewportView(resultString);


        frame.setVisible(true);
    }

}
