import javax.swing.*;
import java.awt.*;

public class PainelArvore extends JPanel {

    private final Arvore arvore;
    private final int RAIO = 22;
    private final int ALTURA_NIVEL = 95;

    public PainelArvore(Arvore arvore) {
        this.arvore = arvore;
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        int altura = (arvore.getAltura() + 1) * ALTURA_NIVEL + 140;
        int largura = (int) Math.pow(2, Math.max(0, arvore.getAltura())) * 60 + 250;
        return new Dimension(Math.max(850, largura), Math.max(650, altura));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (arvore.getRaiz() != null) {
            desenharArvore(g2, arvore.getRaiz(), getWidth() / 2, 50, Math.max(120, getWidth() / 4));
        }
    }

    private void desenharArvore(Graphics2D g, No no, int x, int y, int espacamento) {
        if (no == null) {
            return;
        }

        int proximoEspacamento = Math.max(45, espacamento / 2);

        g.setColor(Color.BLACK);

        if (no.getEsquerda() != null) {
            int proximoX = x - espacamento;
            int proximoY = y + ALTURA_NIVEL;
            g.drawLine(x, y + RAIO, proximoX, proximoY - RAIO);
            desenharArvore(g, no.getEsquerda(), proximoX, proximoY, proximoEspacamento);
        }

        if (no.getDireita() != null) {
            int proximoX = x + espacamento;
            int proximoY = y + ALTURA_NIVEL;
            g.drawLine(x, y + RAIO, proximoX, proximoY - RAIO);
            desenharArvore(g, no.getDireita(), proximoX, proximoY, proximoEspacamento);
        }

        g.setColor(Color.WHITE);
        g.fillOval(x - RAIO, y - RAIO, RAIO * 2, RAIO * 2);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - RAIO, y - RAIO, RAIO * 2, RAIO * 2);

        Font fonteOriginal = g.getFont();
        String textoValor = String.valueOf(no.getValor());
        String textoBalanco = "b: " + arvore.calcularBalanceamento(no);

        g.setFont(fonteOriginal.deriveFont(Font.BOLD, 12f));
        FontMetrics metricaValor = g.getFontMetrics();
        int larguraValor = metricaValor.stringWidth(textoValor);
        g.drawString(textoValor, x - larguraValor / 2, y + 4);

        g.setFont(fonteOriginal.deriveFont(Font.PLAIN, 11f));
        FontMetrics metricaBalanco = g.getFontMetrics();
        int larguraBalanco = metricaBalanco.stringWidth(textoBalanco);
        g.drawString(textoBalanco, x - larguraBalanco / 2, y + RAIO + 16);

        g.setFont(fonteOriginal);
    }
}
