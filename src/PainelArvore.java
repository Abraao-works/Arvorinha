import javax.swing.*;
import java.awt.*;

public class PainelArvore extends JPanel {

    private Arvore arvore;
    private final int RAIO = 20;
    private final int ALTURA_NIVEL = 90;

    public PainelArvore(Arvore arvore) {
        this.arvore = arvore;
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        int altura = (arvore.getAltura() + 1) * ALTURA_NIVEL + 120;
        int largura = (int) Math.pow(2, Math.max(0, arvore.getAltura())) * 55 + 220;
        return new Dimension(Math.max(800, largura), Math.max(600, altura));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (arvore.getRaiz() != null) {
            desenharArvore(g2, arvore.getRaiz(), getWidth() / 2, 40, getWidth() / 4);
        }
    }

    private void desenharArvore(Graphics2D g, No no, int x, int y, int espacamento) {
        if (no == null) {
            return;
        }

        int proximoEspacamento = Math.max(40, espacamento / 2);

        g.setColor(Color.BLACK);

        if (no.getEsquerda() != null) {
            int proximoX = x - espacamento;
            int proximoY = y + ALTURA_NIVEL;
            g.drawLine(x, y, proximoX, proximoY);
            desenharArvore(g, no.getEsquerda(), proximoX, proximoY, proximoEspacamento);
        }

        if (no.getDireita() != null) {
            int proximoX = x + espacamento;
            int proximoY = y + ALTURA_NIVEL;
            g.drawLine(x, y, proximoX, proximoY);
            desenharArvore(g, no.getDireita(), proximoX, proximoY, proximoEspacamento);
        }

        g.setColor(Color.WHITE);
        g.fillOval(x - RAIO, y - RAIO, RAIO * 2, RAIO * 2);

        g.setColor(Color.BLACK);
        g.drawOval(x - RAIO, y - RAIO, RAIO * 2, RAIO * 2);

        String valor = String.valueOf(no.getValor());
        String balanco = "b: " + arvore.calcularBalanceamento(no);

        FontMetrics fm = g.getFontMetrics();
        int larguraValor = fm.stringWidth(valor);
        g.drawString(valor, x - larguraValor / 2, y + 5);

        Font fonteOriginal = g.getFont();
        g.setFont(new Font(fonteOriginal.getName(), Font.PLAIN, 11));
        FontMetrics fmBalanco = g.getFontMetrics();
        int larguraBalanco = fmBalanco.stringWidth(balanco);
        g.drawString(balanco, x - larguraBalanco / 2, y + 35);
        g.setFont(fonteOriginal);
    }
}
