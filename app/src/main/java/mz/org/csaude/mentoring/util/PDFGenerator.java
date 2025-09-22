package mz.org.csaude.mentoring.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.model.ronda.RondaSummary;
import mz.org.csaude.mentoring.model.session.SessionSummary;
import mz.org.csaude.mentoring.model.tutored.Tutored;

public class PDFGenerator {

    public static boolean createPDF(OutputStream out, Context context, List<SessionSummary> sessionSummaryList, Tutored tutored) {
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Add logo
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mz_emblem);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ImageData imageData = ImageDataFactory.create(byteArray);
            Image image = new Image(imageData);
            image.setWidth(75);
            image.setHeight(85);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);

            // Add title and headers
            PdfFont font = PdfFontFactory.createFont();
            document.add(new Paragraph("MINISTÉRIO DA SAÚDE")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("DIRECÇÃO NACIONAL DE SAÚDE PÚBLICA")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Resumo da Sessão de Mentoria")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setUnderline()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Mentorando: " + tutored.getEmployee().getFullName())
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(10)
                    .setUnderline()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.LEFT));

            // Add table with data and colors
            float[] columnWidths = {4, 0.6F, 0.6F, 2.3F};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            // Define cell colors
            DeviceRgb headerColor = new DeviceRgb(0, 122, 204); // Example color for headers
            DeviceRgb blueColor = new DeviceRgb(0xD9, 0xE1, 0xF2); // Example color for cells
            // Red color (assuming RGB values)
            DeviceRgb redColor = new DeviceRgb(0xFF, 0x00, 0x00);

            DeviceRgb whiteColor = new DeviceRgb(0xFF, 0xFF, 0xFF);
            DeviceRgb yellowColor = new DeviceRgb(0xFF, 0xFF, 0x00);
            DeviceRgb greenColor = new DeviceRgb(0x00, 0xFF, 0x00);


            // Add header cells with colors
            table.addHeaderCell(new Cell().add(new Paragraph("Secção")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("SIM")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("NÃO")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("Desempenho da Secção")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));

            for (SessionSummary sessionSummary : sessionSummaryList) {
                table.addCell(new Cell().add(new Paragraph(sessionSummary.getTitle())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sessionSummary.getSimCount()))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sessionSummary.getNaoCount()))).setTextAlignment(TextAlignment.CENTER));
                DeviceRgb color = sessionSummary.getProgressPercentage() < 64 ? redColor : sessionSummary.getProgressPercentage() < 86 ? yellowColor : greenColor;
                table.addCell(new Cell().add(new Paragraph(sessionSummary.getProgressPercentage() + "%")).setBackgroundColor(color).setTextAlignment(TextAlignment.CENTER));
            }

            document.add(table);

            // Add signatures
            document.add(new Paragraph("\nAssinatura do Mentorando:").setFont(font).setFontSize(12));
            document.add(new Paragraph("Assinatura do Mentor:").setFont(font).setFontSize(12));


            document.close();
            return true;
        } catch (Exception e) {
            Log.e("PDFGenerator", "Error writing PDF: " + e.getMessage());
            return false;
        }
    }

    public static boolean createPDF(Context context, List<SessionSummary> sessionSummaryList, Tutored tutored) {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "resumo_da_sessao_" + DateUtilities.getCurrTimeStamp() + ".pdf");

        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Add logo
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mz_emblem);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ImageData imageData = ImageDataFactory.create(byteArray);
            Image image = new Image(imageData);
            image.setWidth(75);
            image.setHeight(85);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);

            // Add title and headers
            PdfFont font = PdfFontFactory.createFont();
            document.add(new Paragraph("MINISTÉRIO DA SAÚDE")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("DIRECÇÃO NACIONAL DE SAÚDE PÚBLICA")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Resumo da Sessão de Mentoria")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setUnderline()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Mentorando: " + tutored.getEmployee().getFullName())
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(10)
                    .setUnderline()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.LEFT));

            // Add table with data and colors
            float[] columnWidths = {4, 0.6F, 0.6F, 2.3F};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            // Define cell colors
            DeviceRgb headerColor = new DeviceRgb(0, 122, 204); // Example color for headers
            DeviceRgb blueColor = new DeviceRgb(0xD9, 0xE1, 0xF2); // Example color for cells
            // Red color (assuming RGB values)
            DeviceRgb redColor = new DeviceRgb(0xFF, 0x00, 0x00);

            DeviceRgb whiteColor = new DeviceRgb(0xFF, 0xFF, 0xFF);
            DeviceRgb yellowColor = new DeviceRgb(0xFF, 0xFF, 0x00);
            DeviceRgb greenColor = new DeviceRgb(0x00, 0xFF, 0x00);


            // Add header cells with colors
            table.addHeaderCell(new Cell().add(new Paragraph("Secção")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("SIM")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("NÃO")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));
            table.addHeaderCell(new Cell().add(new Paragraph("Desempenho da Secção")).setFontColor(whiteColor).setBold().setBackgroundColor(headerColor));

            for (SessionSummary sessionSummary : sessionSummaryList) {
                table.addCell(new Cell().add(new Paragraph(sessionSummary.getTitle())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sessionSummary.getSimCount()))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sessionSummary.getNaoCount()))).setTextAlignment(TextAlignment.CENTER));
                DeviceRgb color = sessionSummary.getProgressPercentage() < 64 ? redColor : sessionSummary.getProgressPercentage() < 86 ? yellowColor : greenColor;
                table.addCell(new Cell().add(new Paragraph(sessionSummary.getProgressPercentage() + "%")).setBackgroundColor(color).setTextAlignment(TextAlignment.CENTER));
            }

            document.add(table);

            // Add signatures
            document.add(new Paragraph("\nAssinatura do Mentorando:").setFont(font).setFontSize(12));
            document.add(new Paragraph("Assinatura do Mentor:").setFont(font).setFontSize(12));

            document.close();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("PDFGenerator", "Error: " + e.getMessage());
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public static boolean createRondaSummary(Context context, List<RondaSummary> rondaSummaryList) {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "relatorio_da_ronda_" + DateUtilities.getCurrTimeStamp() + ".pdf");

        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
            PdfFormXObject placeholder = new PdfFormXObject(new com.itextpdf.kernel.geom.Rectangle(0, 0, 50, 50));
            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderFooterPageEventHandler(placeholder));

            Document document = new Document(pdfDocument);

            // Add logo
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mz_emblem);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            ImageData imageData = ImageDataFactory.create(byteArray);
            Image image = new Image(imageData);
            image.setWidth(75);
            image.setHeight(85);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);

            // Add title and headers
            PdfFont font = PdfFontFactory.createFont();
            document.add(new Paragraph("MINISTÉRIO DA SAÚDE")
                    .setFont(font)
                    .setFontSize(14)
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("DIRECÇÃO NACIONAL DE SAÚDE PÚBLICA")
                    .setFont(font)
                    .setFontSize(14)
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(rondaSummaryList.get(0).getRonda().getHealthFacility().getDescription())
                    .setFont(font)
                    .setFontSize(12)
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Relatório da Ronda/Ciclo de Mentoria")
                    .setFont(font)
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Período da Ronda/Ciclo de Mentoria: " + DateUtilities.formatToDDMMYYYY(rondaSummaryList.get(0).getRonda().getStartDate()) + " à " + DateUtilities.formatToDDMMYYYY(rondaSummaryList.get(0).getRonda().getEndDate()))
                    .setFont(font)
                    .setFontSize(12)
                    .setMarginTop(10)
                    .setFontColor(DeviceRgb.BLACK)
                    .setTextAlignment(TextAlignment.LEFT));



            float[] columnWidths = {0.8F, 1.5F, 0.7F, 0.7F, 0.7F, 0.7F, 0.7F, 1, 1.5F, 1};

            // Define cell colors
            DeviceRgb headerColor = new DeviceRgb(0, 122, 204); // Example color for headers
            DeviceRgb detailsColor = new DeviceRgb(250, 158, 23); // Example color for cells
            DeviceRgb cellColor = new DeviceRgb(0xD9, 0xE1, 0xF2); // Example color for cells
            DeviceRgb whiteColor = new DeviceRgb(0xFF, 0xFF, 0xFF);

            DeviceRgb redColor = new DeviceRgb(0xFF, 0x00, 0x00);

            DeviceRgb yellowColor = new DeviceRgb(0xFF, 0xFF, 0x00);
            DeviceRgb greenColor = new DeviceRgb(0x00, 0xFF, 0x00);

            for (RondaSummary rondaSummary : rondaSummaryList) { // Adjust the column widths
                Table table = new Table(UnitValue.createPercentArray(columnWidths));
                // Add header cells with colors
                table.addHeaderCell(new Cell().add(new Paragraph("NUIT")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Nome")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Avaliação \"Zero\"")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Sessão 1")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Sessão 2")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Sessão 3")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Sessão 4")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Classificação Final")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Mentor")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addHeaderCell(new Cell().add(new Paragraph("Assinatura")).setFontColor(whiteColor).setBackgroundColor(headerColor).setVerticalAlignment(VerticalAlignment.MIDDLE));

                // Add data cells with colors
                table.addCell(new Cell().add(new Paragraph(String.valueOf(rondaSummary.getNuit()))).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph(rondaSummary.getMentee())).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(rondaSummary.getZeroEvaluation()))).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", rondaSummary.getSession1()) + "%")).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph(rondaSummary.getSession2() + "%")).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph(rondaSummary.getSession3() + "%")).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph(rondaSummary.getSession4() + "%")).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph(rondaSummary.getSession4() < 86 ? "Repetir Ronda" : "Graduado")));
                table.addCell(new Cell().add(new Paragraph(rondaSummary.getMentor())).setBackgroundColor(cellColor));
                table.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(cellColor));

                document.add(table);

                // Add second table
                float[] columnWidths2 = {2, 1, 1, 1, 1, 1}; // Adjust the column widths
                Table table2 = new Table(UnitValue.createPercentArray(columnWidths2)).setHorizontalAlignment(HorizontalAlignment.RIGHT);

                // Add header cells with colors
                table2.addHeaderCell(new Cell().add(new Paragraph("Secção")).setFontColor(whiteColor).setBackgroundColor(detailsColor));
                table2.addHeaderCell(new Cell().add(new Paragraph("Sessão 1")).setFontColor(whiteColor).setBackgroundColor(detailsColor));
                table2.addHeaderCell(new Cell().add(new Paragraph("Sessão 2")).setFontColor(whiteColor).setBackgroundColor(detailsColor));
                table2.addHeaderCell(new Cell().add(new Paragraph("Sessão 3")).setFontColor(whiteColor).setBackgroundColor(detailsColor));
                table2.addHeaderCell(new Cell().add(new Paragraph("Sessão 4")).setFontColor(whiteColor).setBackgroundColor(detailsColor));
                table2.addHeaderCell(new Cell().add(new Paragraph("Classificação")).setFontColor(whiteColor).setBackgroundColor(detailsColor));


                    Map<String, List<String>> summaryDetails = new HashMap<>();

                    for (Map.Entry<Integer, List<SessionSummary>> entry : rondaSummary.getSummaryDetails().entrySet()) {
                        for (SessionSummary summary : entry.getValue()) {
                            if (!summaryDetails.containsKey(String.valueOf(summary.getTitle()))) {
                                summaryDetails.put(String.valueOf(summary.getTitle()), new ArrayList<>());
                            }
                            summaryDetails.get(summary.getTitle()).add(summary.getProgressPercentage() + "%");
                        }
                    }

                    for (Map.Entry<String, List<String>> entry : summaryDetails.entrySet()) {
                        table2.addCell(new Cell().add(new Paragraph(entry.getKey())).setBackgroundColor(cellColor));
                        List<String> sessionPercentages = entry.getValue();
                        for (int i = 0; i < 4; i++) {
                            if (i < sessionPercentages.size()) {
                                table2.addCell(new Cell().add(new Paragraph(sessionPercentages.get(i))).setBackgroundColor(cellColor));
                            } else {
                                table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(cellColor)); // Placeholder for missing sessions
                            }
                        }
                        table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(cellColor)); // Placeholder for Classificação
                    }

                    document.add(table2);

            }

            document.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
