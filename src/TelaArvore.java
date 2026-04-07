import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TelaArvore extends JFrame {

    private Arvore arvore;
    private JTextField campoNumero;
    private PainelArvore painelArvore;
    private JScrollPane scrollPane;

    public TelaArvore() {
        arvore = new Arvore();

        setTitle("Árvore Binária");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Painel Superior de Controles
        JPanel painelControle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelControle.setBorder(BorderFactory.createTitledBorder("Operações"));

        JLabel label = new JLabel("Valor:");
        campoNumero = new JTextField(5);

        JButton botaoInserir = new JButton("Inserir");
        JButton botaoLimpar = new JButton("Limpar");
        JButton botaoSalvar = new JButton("Salvar TXT");
        JButton botaoImportar = new JButton("Importar TXT");
        JButton botaoInfo = new JButton("Info Árvore");
        JButton botaoInverter = new JButton("Inverter Árvore");

        painelControle.add(label);
        painelControle.add(campoNumero);
        painelControle.add(botaoInserir);
        painelControle.add(botaoLimpar);
        painelControle.add(botaoSalvar);
        painelControle.add(botaoImportar);
        painelControle.add(botaoInfo);
        painelControle.add(botaoInverter);
        

        add(painelControle, BorderLayout.NORTH);

        // Painel Central com Scroll
        painelArvore = new PainelArvore(arvore);
        scrollPane = new JScrollPane(painelArvore);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        botaoInserir.addActionListener(e -> inserirValor());
        botaoLimpar.addActionListener(e -> limparArvore());
        botaoSalvar.addActionListener(e -> salvarArvore());
        botaoImportar.addActionListener(e -> importarArvore());
        botaoInfo.addActionListener(e -> mostrarInfo());
        campoNumero.addActionListener(e -> inserirValor());
        botaoInverter.addActionListener(e -> inverterArvore());
    }
    
    private void importarArvore() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Árvore de TXT");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                arvore.importarDeTxt(path);
                atualizarVisualizacao();
                JOptionPane.showMessageDialog(this, "Árvore importada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao importar arquivo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void inserirValor() {
        try {
            String texto = campoNumero.getText().trim();
            if (texto.isEmpty()) return;

            int numero = Integer.parseInt(texto);

            if (numero == -1) {
                JOptionPane.showMessageDialog(this, "O valor -1 não é permitido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean inserido = arvore.inserir(numero);

            if (!inserido) {
                JOptionPane.showMessageDialog(this, "Número " + numero + " já existe!", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            campoNumero.setText("");
            atualizarVisualizacao();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite apenas números inteiros.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparArvore() {
        arvore.limpar();
        atualizarVisualizacao();
    }

    private void salvarArvore() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Árvore em TXT");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".txt")) path += ".txt";
                arvore.salvarEmTxt(path);
                JOptionPane.showMessageDialog(this, "Árvore salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar arquivo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void inverterArvore() {
        if (arvore.getRaiz() == null) {
            JOptionPane.showMessageDialog(this, "Árvore vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        arvore.inverter();
        atualizarVisualizacao();
        }

    private void mostrarInfo() {
        if (arvore.getRaiz() == null) {
            JOptionPane.showMessageDialog(this, "Árvore vazia.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Geral ---\n");
        sb.append("Tipo da Árvore: ").append(arvore.getTipoArvore()).append("\n");
        sb.append("Altura da Árvore: ").append(arvore.getAltura()).append("\n");
        sb.append("Nível da Árvore: ").append(arvore.getNivelArvore()).append("\n");
        sb.append("Profundidade da Árvore: ").append(arvore.getProfundidadeArvore()).append("\n");
        sb.append("\n--- Percursos ---\n");
        sb.append("Pré-Ordem: ").append(arvore.preOrdem()).append("\n");
        sb.append("Em-Ordem: ").append(arvore.emOrdem()).append("\n");
        sb.append("Pós-Ordem: ").append(arvore.posOrdem()).append("\n");
        sb.append("Representação Parênteses: ").append(arvore.getRepresentacaoParenteses()).append("\n");
        sb.append("\n--- Detalhes por Nó ---\n");

        adicionarDetalhesNos(arvore.getRaiz(), sb);

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scroll, "Estatísticas Detalhadas da Árvore", JOptionPane.INFORMATION_MESSAGE);
    }

    private void adicionarDetalhesNos(No no, StringBuilder sb) {
    if (no == null) return;

    int v = no.getValor();

    sb.append(String.format(
            "Valor: %-4d | Tipo: %-7s | Nível: %d | Altura: %d | Profundidade: %d\n",
            v,
            no.getTipo(),
            arvore.getNivel(v),
            no.getAltura(),
            arvore.getProfundidadeNo(v)
    ));

    adicionarDetalhesNos(no.getEsquerda(), sb);
    adicionarDetalhesNos(no.getDireita(), sb);
}

    private void atualizarVisualizacao() {
        painelArvore.revalidate();
        painelArvore.repaint();
    }
}