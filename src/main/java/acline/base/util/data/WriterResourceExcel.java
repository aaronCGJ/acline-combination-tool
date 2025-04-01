package acline.base.util.data;

import acline.base.util.dataobject.IpCityDTO;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.format.*;
import jxl.write.*;
import jxl.write.WritableFont.FontName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Aaron Chen
 * @date：2023/5/10
 * @Description:TODO
 * @Version 1.0
 */
public class WriterResourceExcel {
    public  static  void writerResourceExcel(Map<String,Object> resourceType) throws Exception {
        String resource_type = (String) resourceType.get("resource_type");
        Map<String, String> map = System.getenv();
        String userName = map.get("HOME");
        File file = new File(userName+"/data/resource_" + resource_type + ".xls");
        file.createNewFile();
        OutputStream outputStream = new FileOutputStream(file);
        WritableWorkbook writableWorkbook = Workbook.createWorkbook(outputStream);
        WritableSheet sheet = writableWorkbook.createSheet(resource_type, 0);
        WritableFont f;
        WritableCellFormat cf;
        FontName fn = WritableFont.COURIER;
        // 表头
        f = new WritableFont(fn, 12, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.DARK_RED);
        cf = new WritableCellFormat(f);
        cf.setVerticalAlignment(VerticalAlignment.CENTRE);
        cf.setBorder(Border.ALL, BorderLineStyle.THIN);
        cf.setAlignment(Alignment.CENTRE);

        sheet.addCell(new Label(0, 0, "IP", cf));
        sheet.addCell(new Label(1, 0, "city", cf));
        f = new WritableFont(fn, 11, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.DARK_PURPLE);
        //写数据
        int maxLength = 0;
        List<IpCityDTO> ip_city = (List<IpCityDTO>) resourceType.get("ip_city");
        for (IpCityDTO resourceinfo : ip_city) {
            // 样式
            cf = new WritableCellFormat(f);
            cf.setVerticalAlignment(VerticalAlignment.CENTRE);
            cf.setBorder(Border.ALL, BorderLineStyle.THIN);
            //cf.setAlignment(Alignment.LEFT);
            //
            int rows = sheet.getRows();

            cf.setBackground(Colour.WHITE);
            sheet.addCell(new Label(0, rows, resourceinfo.getIp(), cf));
            sheet.addCell(new Label(1, rows, resourceinfo.getCity(), cf));
        }
        // 列宽
        CellView cellView = new CellView();
        cellView.setAutosize(true); //设置自动大小
        sheet.setColumnView(0, cellView);
        sheet.setColumnView(1, cellView);

        // 行高
        sheet.getSettings().setDefaultRowHeight((int) (256 * 1.25));
        // 加入到文件中
        writableWorkbook.write();
        // 关闭文件，释放资源
        writableWorkbook.close();
    }

}
