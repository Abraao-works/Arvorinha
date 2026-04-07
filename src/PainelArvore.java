import javax.swing.*;
import java.awt.*;

public class PainelArvore extends JPanel {

    private Arvore arvore;
    private final int RAIO = 20;
    private final int ALTURA_NIVEL = 80;

    public PainelArvore(Arvore arvore) {
        this.arvore = arvore;
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getPreferredSize() {
        int altura = (arvore.getAltura() + 1) * ALTURA_NIVEL + 100;
        // Estimativa de largura baseada na altura (2^altura * espaçamento base)
        int largura = (int) Math.pow(2, Math.max(0, arvore.getAltura())) * 50 + 200;
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
        if (no == null) return;

        g.setColor(Color.BLACK);

        // Filho esquerdo
        if (no.getEsquerda() != null) {
            int proximoX = x - espacamento;
            int proximoY = y + ALTURA_NIVEL;
            g.drawLine(x, y, proximoX, proximoY);
            desenharArvore(g, no.getEsquerda(), proximoX, proximoY, espacamento / 2);
        }

        // Filho direito
        if (no.getDireita() != null) {
            int proximoX = x + espacamento;
            int proximoY = y + ALTURA_NIVEL;
            g.drawLine(x, y, proximoX, proximoY);
            desenharArvore(g, no.getDireita(), proximoX, proximoY, espacamento / 2);
        }

        // Nó (círculo)
        g.setColor(Color.WHITE);
        g.fillOval(x - RAIO, y - RAIO, RAIO * 2, RAIO * 2);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - RAIO, y - RAIO, RAIO * 2, RAIO * 2);

        String texto = String.valueOf(no.getValor());
        FontMetrics fm = g.getFontMetrics();
        int larguraTexto = fm.stringWidth(texto);

        g.drawString(texto, x - larguraTexto / 2, y + 5);
    }
}
