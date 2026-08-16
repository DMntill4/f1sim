package f1sim.ui;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

// Sintetizador de audio nativo Java para generar el sonido característico de motor de F1 V10
public class F1SonidoMotor {

    private static final int SAMPLE_RATE = 22050;
    private static SourceDataLine line;
    private static volatile boolean reproduciendo = false;
    private static volatile boolean silenciado = true;
    private static volatile double velocidadActualKmh = 180.0;
    private static Thread hiloAudio;

    public static synchronized void iniciarMotor() {
        if (reproduciendo) return;
        reproduciendo = true;

        hiloAudio = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
                line = AudioSystem.getSourceDataLine(format);
                line.open(format, 2048);
                line.start();

                byte[] buffer = new byte[512];
                double fase = 0.0;

                while (reproduciendo) {
                    if (silenciado) {
                        Thread.sleep(50);
                        continue;
                    }

                    // Calcular frecuencia del tono en función de la velocidad (de 120Hz a 800Hz)
                    double freq = 130.0 + (Math.max(0, velocidadActualKmh) * 2.2);

                    for (int i = 0; i < buffer.length; i++) {
                        fase += (2.0 * Math.PI * freq) / SAMPLE_RATE;
                        // Mezcla de onda diente de sierra y armónicos para simular el bramido V10
                        double muestra = (Math.sin(fase) * 0.4) + (Math.sin(fase * 2.0) * 0.3) + (Math.sin(fase * 3.0) * 0.15);
                        buffer[i] = (byte) (muestra * 50.0);
                    }

                    line.write(buffer, 0, buffer.length);
                }

                line.drain();
                line.close();
            } catch (Exception e) {
                // Audio no soportado o silenciado en el SO
            }
        }, "f1-audio-motor");

        hiloAudio.setDaemon(true);
        hiloAudio.start();
    }

    public static void actualizarVelocidad(double velocidadKmh) {
        velocidadActualKmh = velocidadKmh;
    }

    public static void setSilenciado(boolean mute) {
        silenciado = mute;
    }

    public static boolean isSilenciado() {
        return silenciado;
    }

    public static synchronized void detenerMotor() {
        reproduciendo = false;
        if (line != null) {
            try {
                line.stop();
            } catch (Exception ignored) {}
        }
    }
}
