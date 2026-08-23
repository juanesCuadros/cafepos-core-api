package com.cafepos.core.restaurante.application;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Genera el QR del menu digital al vuelo en cada GET, en vez de persistir
 * un archivo de imagen — no hay proveedor de object storage decidido
 * todavia (mismo bloqueo que POST /uploads de productos_menu, ver
 * api_04_productos_menu.md). Temporal: cuando exista un proveedor real,
 * esto deberia pasar a generarse una vez y guardarse como archivo,
 * sirviendo una URL real en vez de un data URI embebido en la respuesta.
 */
final class QrCodeGenerator {

    private static final int TAMANO_PX = 300;

    private QrCodeGenerator() {
    }

    static String generarDataUri(String contenido) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(contenido, BarcodeFormat.QR_CODE, TAMANO_PX, TAMANO_PX);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", buffer);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(buffer.toByteArray());
        } catch (WriterException | IOException ex) {
            throw new IllegalStateException("No se pudo generar el QR del menú digital", ex);
        }
    }
}
