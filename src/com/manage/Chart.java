package com.manage;

import com.data.app.Log;
import com.data.db.Database;
import static com.data.db.Database.DB_NAME;
import com.media.Audio;
import com.window.panels.LaporanJual;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;

/**
 *
 * @author Achmad Baihaqi
 * @since 2022-10-12
 */
public class Chart {
    private final String namadb = Database.DB_NAME;
    private Connection con;
    private Statement stmt;
    private ResultSet res;
    private final Color C_MAKANAN = new Color(10,255,108), C_MINUMAN = new Color(2,99,255), 
                        C_SNACK = new Color(255,51,102), C_ATK = new Color(255,233,35),
                        BG_CHART = Color.WHITE;
    
    private final Font F_PRODUK = new Font("Ebrima", 1, 22);
    public Chart(){
        koneksi();
    }
    private void koneksi() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + this.namadb, "root", "");
            this.stmt = con.createStatement();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void closeKoneksi(){
        try{
            // Mengecek apakah conn kosong atau tidak, jika tidak maka akan diclose
            if(this.con != null){
                this.con.close();
            }
            // Mengecek apakah stat kosong atau tidak, jika tidak maka akan diclose
            if(this.stmt != null){
                this.stmt.close();
            }
            // Mengecek apakah res koson atau tidak, jika tidak maka akan diclose
            if(this.res != null){
                this.res.close();
            }
            
        Log.addLog(String.format("Berhasil memutus koneksi dari Database '%s'.", DB_NAME));
        }catch(SQLException ex){
            Audio.play(Audio.SOUND_ERROR);
            JOptionPane.showMessageDialog(null, "Terjadi Kesalahan!\n\nError message : "+ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int getTotal(String table, String kolom, String kondisi) {
        try {
            koneksi();
            int data = 0;
            String sql = "SELECT SUM(" + kolom + ") AS total FROM " + table + " " + kondisi;
//            System.out.println(sql);
            this.res = this.stmt.executeQuery(sql);
            while (this.res.next()) {
//                System.out.println("data ditemukan");
                data = this.res.getInt("total");
            }
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException n) {
//            n.printStackTrace();
            System.out.println("errorr ");
            return 0;
        }
        return -1;
    }
    //digunakan untuk menampilkan piechart
    public void showPieChart(JPanel panel, String title, Font font, double makanan, double minuman, double snack, double atk){
        
        //create dataset
        DefaultPieDataset barDataset = new DefaultPieDataset();
        if(makanan > 0){
            barDataset.setValue( "Makanan", new Double(makanan));  
        }
        if(minuman > 0){
            barDataset.setValue( "Minuman", new Double(minuman));   
        }
        if(snack > 0){
            barDataset.setValue( "Snack", new Double(snack));    
        }
        if(atk > 0){
            barDataset.setValue( "ATK", new Double(atk));  
        }
        //create chart
        JFreeChart piechart = ChartFactory.createPieChart("Penjualan Produk",barDataset, false,true,false);//explain
        piechart.setTitle(new TextTitle(title, font));

        //changing pie chart blocks colors
        PiePlot piePlot =(PiePlot) piechart.getPlot();
        piePlot.setSectionPaint("Makanan", this.C_MAKANAN);
        piePlot.setSectionPaint("Minuman", this.C_MINUMAN);
        piePlot.setSectionPaint("Snack", this.C_SNACK);
        piePlot.setSectionPaint("ATK", this.C_ATK);
        piePlot.setBackgroundPaint(this.BG_CHART);

        //create chartPanel to display chart(graph)
        ChartPanel barChartPanel = new ChartPanel(piechart);
        panel.removeAll();
        panel.add(barChartPanel, BorderLayout.CENTER);
        panel.validate();
    }

    public void showPieChart(JPanel panel, String title, double makanan, double minuman, double snack, double atk){
        this.showPieChart(panel, title, F_PRODUK, makanan, minuman, snack, atk);
    }
    //digunakan untuk menampilkan line chart berdasarkan mingguan
    public void showLineChart(JPanel panel, Object[] obj) throws ParseException {
        //create dataset for the graph
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String m1h1, m1h2,m2h1,m2h2,m3h1,m3h2,m4h1,m4h2,m5h1,m5h2,m6h1,m6h2;
        int tahun1,tahun2,tahun3,tahun4,tahun5,tahun6,bulan1,bulan2,bulan3,bulan4,bulan5,bulan6,hari1,hari2,hari3,hari4,hari5,hari6;
//        Object[] obj = waktu.getMinggu(bulan, tahun);
        if (obj.length == 4) {
//            System.out.println("minggu ada 4");
            m1h1 = obj[0].toString().substring(0,10);
            m1h2 = obj[0].toString().substring(11,21);
            hari1 = Integer.parseInt(m1h2.substring(8));
            bulan1 = Integer.parseInt(m1h2.substring(5,7));
            tahun1 = Integer.parseInt(m1h2.substring(0,4));
            
            m2h1 = obj[1].toString().substring(0,10);
            m2h2 = obj[1].toString().substring(11,21);
            hari2 = Integer.parseInt(m2h2.substring(8));
            bulan2 = Integer.parseInt(m2h2.substring(5,7));
            tahun2 = Integer.parseInt(m2h2.substring(0,4));
            
            m3h1 = obj[2].toString().substring(0,10);
            m3h2 = obj[2].toString().substring(11,21);
            hari3 = Integer.parseInt(m3h2.substring(8));
            bulan3 = Integer.parseInt(m3h2.substring(5,7));
            tahun3 = Integer.parseInt(m3h2.substring(0,4));
            
            m4h1 = obj[3].toString().substring(0,10);
            m4h2 = obj[3].toString().substring(11,21);
            hari4 = Integer.parseInt(m4h2.substring(8));
            bulan4 = Integer.parseInt(m4h2.substring(5,7));
            tahun4 = Integer.parseInt(m4h2.substring(0,4));
            //ambil data dari database
            int minggu1 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m1h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun1,bulan1,hari1+1)+"'");
            int minggu2 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m2h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun2,bulan2,hari2+1)+"'");
            int minggu3 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m3h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun3,bulan3,hari3+1)+"'");
            int minggu4 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m4h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun4,bulan4,hari4+1)+"'");
            
            if(minggu1 > 0){
                dataset.addValue(minggu1, "Amount", "Minggu 1");
            }else{
                dataset.addValue(0, "Amount", "Minggu 1");
            }
            if(minggu2 > 0){
                dataset.addValue(minggu2, "Amount", "Minggu 2");
            }else{
                dataset.addValue(0, "Amount", "Minggu 2");
            }
            if(minggu3 > 0){
                dataset.addValue(minggu3, "Amount", "Minggu 3");
            }else{
                dataset.addValue(0, "Amount", "Minggu 3");
            }
            if(minggu4 > 0){
                dataset.addValue(minggu4, "Amount", "Minggu 4");
            }else{
                dataset.addValue(0, "Amount", "Minggu 4");
            }
        }else if (obj.length == 5) {
//            System.out.println("minggu ada 5");
            m1h1 = obj[0].toString().substring(0,10);
            m1h2 = obj[0].toString().substring(11,21);
            hari1 = Integer.parseInt(m1h2.substring(8));
            bulan1 = Integer.parseInt(m1h2.substring(5,7));
            tahun1 = Integer.parseInt(m1h2.substring(0,4));
            
            m2h1 = obj[1].toString().substring(0,10);
            m2h2 = obj[1].toString().substring(11,21);
            hari2 = Integer.parseInt(m2h2.substring(8));
            bulan2 = Integer.parseInt(m2h2.substring(5,7));
            tahun2 = Integer.parseInt(m2h2.substring(0,4));
            
            m3h1 = obj[2].toString().substring(0,10);
            m3h2 = obj[2].toString().substring(11,21);
            hari3 = Integer.parseInt(m3h2.substring(8));
            bulan3 = Integer.parseInt(m3h2.substring(5,7));
            tahun3 = Integer.parseInt(m3h2.substring(0,4));
            
            m4h1 = obj[3].toString().substring(0,10);
            m4h2 = obj[3].toString().substring(11,21);
            hari4 = Integer.parseInt(m4h2.substring(8));
            bulan4 = Integer.parseInt(m4h2.substring(5,7));
            tahun4 = Integer.parseInt(m4h2.substring(0,4));
            
            m5h1 = obj[4].toString().substring(0,10);
            m5h2 = obj[4].toString().substring(11,21);
            hari5 = Integer.parseInt(m5h2.substring(8));
            bulan5 = Integer.parseInt(m5h2.substring(5,7));
            tahun5 = Integer.parseInt(m5h2.substring(0,4));
            
            int minggu1 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m1h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun1,bulan1,hari1+1)+"'");
            int minggu2 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m2h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun2,bulan2,hari2+1)+"'");
            int minggu3 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m3h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun3,bulan3,hari3+1)+"'");
            int minggu4 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m4h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun4,bulan4,hari4+1)+"'");
            int minggu5 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m5h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun5,bulan5,hari5+1)+"'");
            if(minggu1 > 0){
                dataset.addValue(minggu1, "Amount", "Minggu 1");
            }else{
                dataset.addValue(0, "Amount", "Minggu 1");
            }
            if(minggu2 > 0){
                dataset.addValue(minggu2, "Amount", "Minggu 2");
            }else{
                dataset.addValue(0, "Amount", "Minggu 2");
            }
            if(minggu3 > 0){
                dataset.addValue(minggu3, "Amount", "Minggu 3");
            }else{
                dataset.addValue(0, "Amount", "Minggu 3");
            }
            if(minggu4 > 0){
                dataset.addValue(minggu4, "Amount", "Minggu 4");
            }else{
                dataset.addValue(0, "Amount", "Minggu 4");
            }
            if(minggu5 > 0){
                dataset.addValue(minggu5, "Amount", "Minggu 5");
            }else{
                dataset.addValue(0, "Amount", "Minggu 5");
            }
        }else if (obj.length == 6) {
//            System.out.println("minggu ada 6");
            m1h1 = obj[0].toString().substring(0,10);
            m1h2 = obj[0].toString().substring(11,21);
            hari1 = Integer.parseInt(m1h2.substring(8));
            bulan1 = Integer.parseInt(m1h2.substring(5,7));
            tahun1 = Integer.parseInt(m1h2.substring(0,4));
            
            m2h1 = obj[1].toString().substring(0,10);
            m2h2 = obj[1].toString().substring(11,21);
            hari2 = Integer.parseInt(m2h2.substring(8));
            bulan2 = Integer.parseInt(m2h2.substring(5,7));
            tahun2 = Integer.parseInt(m2h2.substring(0,4));
            
            m3h1 = obj[2].toString().substring(0,10);
            m3h2 = obj[2].toString().substring(11,21);
            hari3 = Integer.parseInt(m3h2.substring(8));
            bulan3 = Integer.parseInt(m3h2.substring(5,7));
            tahun3 = Integer.parseInt(m3h2.substring(0,4));
            
            m4h1 = obj[3].toString().substring(0,10);
            m4h2 = obj[3].toString().substring(11,21);
            hari4 = Integer.parseInt(m4h2.substring(8));
            bulan4 = Integer.parseInt(m4h2.substring(5,7));
            tahun4 = Integer.parseInt(m4h2.substring(0,4));
            
            m5h1 = obj[4].toString().substring(0,10);
            m5h2 = obj[4].toString().substring(11,21);
            hari5 = Integer.parseInt(m5h2.substring(8));
            bulan5 = Integer.parseInt(m5h2.substring(5,7));
            tahun5 = Integer.parseInt(m5h2.substring(0,4));
            
            m6h1 = obj[5].toString().substring(0,10);
            m6h2 = obj[5].toString().substring(11,21);
            hari6 = Integer.parseInt(m6h2.substring(8));
            bulan6 = Integer.parseInt(m6h2.substring(5,7));
            tahun6 = Integer.parseInt(m6h2.substring(0,4));
            
            int minggu1 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m1h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun1,bulan1,hari1+1)+"'");
            int minggu2 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m2h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun2,bulan2,hari2+1)+"'");
            int minggu3 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m3h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun3,bulan3,hari3+1)+"'");
            int minggu4 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m4h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun4,bulan4,hari4+1)+"'");
            int minggu5 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m5h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun5,bulan5,hari5+1)+"'");
            int minggu6 = getTotal("transaksi_jual", "keuntungan", "WHERE tanggal >= '"+ m6h1 +"' AND tanggal <= '"+ String.format("%s-%s-%s",tahun6,bulan6,hari6+1)+"'");
            
            if(minggu1 > 0){
                dataset.addValue(minggu1, "Amount", "Minggu 1");
            }else{
                dataset.addValue(0, "Amount", "Minggu 1");
            }
            if(minggu2 > 0){
                dataset.addValue(minggu2, "Amount", "Minggu 2");
            }else{
                dataset.addValue(0, "Amount", "Minggu 2");
            }
            if(minggu3 > 0){
                dataset.addValue(minggu3, "Amount", "Minggu 3");
            }else{
                dataset.addValue(0, "Amount", "Minggu 3");
            }
            if(minggu4 > 0){
                dataset.addValue(minggu4, "Amount", "Minggu 4");
            }else{
                dataset.addValue(0, "Amount", "Minggu 4");
            }
            if(minggu5 > 0){
                dataset.addValue(minggu5, "Amount", "Minggu 5");
            }else{
                dataset.addValue(0, "Amount", "Minggu 5");
            }
            if(minggu6 > 0){
                dataset.addValue(minggu6, "Amount", "Minggu 6");
            }else{
                dataset.addValue(0, "Amount", "Minggu 6");
            }
        }

        //create chart
        JFreeChart linechart = ChartFactory.createLineChart("", "", "",
                dataset, PlotOrientation.VERTICAL, false, true, false);
//        linechart.setTitle(new TextTitle("Penjualan Produk Minggu Ini", new java.awt.Font("Ebrima", 1, 21)));

        //create plot object
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        lineCategoryPlot.setRangeGridlinePaint(Color.BLUE);
        lineCategoryPlot.setBackgroundPaint(Color.WHITE);

        //create render object to change the moficy the line properties like color
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255, 2, 9);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        
        //create chartPanel to display chart(graph)
        ChartPanel lineChartPanel = new ChartPanel(linechart);
        panel.removeAll();
        panel.add(lineChartPanel, BorderLayout.CENTER);
        panel.validate();
    }
    
    public void lineChartPenjualan(JPanel panel){
        HashMap<String, Integer> hash = new HashMap();
        hash.put("Kamis", 200);
        hash.put("Jumat", 150);
        hash.put("Sabtu", 58);
        hash.put("Minggu", 30);
        hash.put("Senin", 180);
        hash.put("Selasa", 250);
        hash.put("Rabu", 250);
        
        this.showLineChart1(panel, hash);
    }

    
    public void showLineChart1(JPanel panel, HashMap<String, Integer> hash){
        //create dataset for the graph
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.setValue(200, "Amount", "Kamis");
//        dataset.setValue(150, "Amount", "Jumat");
//        dataset.setValue(58, "Amount", "Sabtu");
//        dataset.setValue(30, "Amount", "Minggu");
//        dataset.setValue(180, "Amount", "Senin");
//        dataset.setValue(250, "Amount", "Selasa");
//        dataset.setValue(250, "Amount", "Rabu");
        
        String key;
        int valKey;
        StringTokenizer token;
        Object[] buff = hash.entrySet().toArray();
        
        for(Object obj : buff){
            token = new StringTokenizer(obj.toString(), "=");
            key = token.nextToken();
            valKey = Integer.parseInt(token.nextToken());
            dataset.setValue(valKey, "Amount", key);
        }
        
        //create chart
        JFreeChart linechart = ChartFactory.createLineChart("Penjualan Seminggu Terakhir","Hari","Jumlah", 
                dataset, PlotOrientation.VERTICAL, false,true,false);
        linechart.setTitle(new TextTitle("Line Chart", new java.awt.Font("Ebrima", 1, 22)));
        
        //create plot object
        CategoryPlot lineCategoryPlot = linechart.getCategoryPlot();
        lineCategoryPlot.setRangeGridlinePaint(Color.BLUE);
        lineCategoryPlot.setBackgroundPaint(Color.WHITE);
        
        //create render object to change the moficy the line properties like color
        LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) lineCategoryPlot.getRenderer();
        Color lineChartColor = new Color(255,2,9);
        lineRenderer.setSeriesPaint(0, lineChartColor);
        
         //create chartPanel to display chart(graph)
        ChartPanel lineChartPanel = new ChartPanel(linechart);
        panel.removeAll();
        panel.add(lineChartPanel, BorderLayout.CENTER);
        panel.validate();
    }

    public void showBarChart(JPanel panel){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(200, "Amount", "january");
        dataset.setValue(150, "Amount", "february");
        dataset.setValue(18, "Amount", "march");
        dataset.setValue(100, "Amount", "april");
        dataset.setValue(80, "Amount", "may");
        dataset.setValue(250, "Amount", "june");
        
        JFreeChart chart = ChartFactory.createBarChart("contribution","monthly","amount", 
                dataset, PlotOrientation.VERTICAL, false,true,false);
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        //categoryPlot.setRangeGridlinePaint(Color.BLUE);
        categoryPlot.setBackgroundPaint(Color.WHITE);
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        Color clr3 = new Color(204,0,51);
        renderer.setSeriesPaint(0, clr3);
        
        ChartPanel barpChartPanel = new ChartPanel(chart);
        panel.removeAll();
        panel.add(barpChartPanel, BorderLayout.CENTER);
        panel.validate();
        
        
    }
    
    public static void main(String[] args) {
        
        HashMap<String, Integer> hash = new HashMap();
        hash.put("Senin", 10);
        hash.put("Selasa", 20);
        
        System.out.println(hash.toString());
        
        Object[] values = hash.values().toArray(),
                 key = hash.keySet().toArray();
        
        Object[] val = hash.entrySet().toArray();
        
        StringTokenizer token;
        for(Object b : val){
            token = new StringTokenizer(b.toString(), "=");
            System.out.printf("Key : %s\nValue : %s\n\n", token.nextToken(), token.nextToken());
        }
        
    }
    
}
