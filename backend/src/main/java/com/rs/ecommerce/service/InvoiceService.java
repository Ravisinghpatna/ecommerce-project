package com.rs.ecommerce.service;
 
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.rs.ecommerce.model.Order;
import com.rs.ecommerce.model.OrderItem;

import org.springframework.stereotype.Service;
 
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
 
/**
 * InvoiceService — Order ke data se ek PDF invoice banata hai.
 * NOTE: Default PDF fonts (Helvetica) me ₹ symbol properly render nahi hota
 * (encoding issue), isliye hum "Rs." likhte hain — real projects me isके
 * liye Unicode font (e.g. Noto Sans) load karni padti hai.
 */
@Service
public class InvoiceService {
 
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
 
    public byte[] generateInvoice(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
 
        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();
 
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10);
            Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
 
            // ---------------- Header ----------------
            document.add(new Paragraph("Ravi Enterprises", titleFont));
            document.add(new Paragraph("Tax Invoice", headingFont));
            document.add(Chunk.NEWLINE);
 
            document.add(new Paragraph("Order #" + order.getId(), boldFont));
            document.add(new Paragraph("Order Date: " + order.getOrderDate().format(DATE_FORMAT), normalFont));
            document.add(new Paragraph("Status: " + order.getStatus(), normalFont));
            if (order.getTrackingId() != null) {
                document.add(new Paragraph("Tracking ID: " + order.getTrackingId(), normalFont));
            }
            document.add(Chunk.NEWLINE);
 
            // ---------------- Bill To ----------------
            document.add(new Paragraph("Bill To:", headingFont));
            document.add(new Paragraph(order.getCustomerName(), normalFont));
            document.add(new Paragraph(order.getCustomerEmail(), normalFont));
            document.add(new Paragraph(order.getCustomerPhone(), normalFont));
            document.add(new Paragraph(order.getShippingAddress(), normalFont));
            document.add(Chunk.NEWLINE);
 
            // ---------------- Items Table ----------------
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 1, 2, 2});
 
            addHeaderCell(table, "Product", boldFont);
            addHeaderCell(table, "Qty", boldFont);
            addHeaderCell(table, "Price", boldFont);
            addHeaderCell(table, "Subtotal", boldFont);
 
            for (OrderItem item : order.getItems()) {
                BigDecimal subtotal = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()));
                table.addCell(new Phrase(item.getProduct().getName(), normalFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                table.addCell(new Phrase("Rs. " + item.getPriceAtPurchase(), normalFont));
                table.addCell(new Phrase("Rs. " + subtotal, normalFont));
            }
            document.add(table);
            document.add(Chunk.NEWLINE);
 
            // ---------------- Total ----------------
            Paragraph total = new Paragraph("Total: Rs. " + order.getTotalAmount(), headingFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
 
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Thank you for shopping with us! Have a good day.", normalFont));
 
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
 
        return out.toByteArray();
    }
 
    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(240, 240, 240));
        cell.setPadding(6);
        table.addCell(cell);
    }
}
 