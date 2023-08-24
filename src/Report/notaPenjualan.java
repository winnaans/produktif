package Report;

import com.manage.Waktu;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.*;
public class notaPenjualan {
    Waktu waktu = new Waktu();
    public static void main(String[] args) {
        JasperReport jReport;
        JasperPrint jPrint;
        try {
            jReport = JasperCompileManager.compileReport(
                    "jasperreports_demo.jrxml");
            jPrint = JasperFillManager.fillReport(
                    jReport, new HashMap(), new JREmptyDataSource());
            JasperExportManager.exportReportToPdfFile(
                    jPrint, "reports/simple_report.pdf");
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
