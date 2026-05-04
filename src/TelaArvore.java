import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TelaArvore extends JFrame {

    private final Arvore arvore;
    private JTextField campoNumero;
    private JComboBox<String> comboModo;
    private JTextArea areaLog;
    private PainelArvore painelArvore;

    public TelaArvore() {
        arvore = new Arvore();

        setTitle("Arvore Binaria e AVL");
        setSize(1000, 730);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        montarPainelControle();
        montarPainelArvore();
        montarPainelLog();
    }

    private void montarPainelControle() {
        JPanel painelControle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelControle.setBorder(BorderFactory.createTitledBorder("Operacoes"));

        JLabel labelValor = new JLabel("Valor:");
        campoNumero = new JTextField(5);

        JLabel labelModo = new JLabel("Tipo:");
        comboModo = new JComboBox<>(new String[]{"Binaria Normal", "AVL"});

        JButton botaoInserir = new JButton("Inserir");
        JButton botaoLimpar = new JButton("Limpar");
        JButton botaoSalvar = new JButton("Salvar TXT");
        JButton botaoImportar = new JButton("Importar TXT");
        JButton botaoInfo = new JButton("Info Arvore");
        JButton botaoInverter = new JButton("Inverter Arvore");
        JButton botaoQuestoesAVL = new JButton("Questoes AVL");

        painelControle.add(labelValor);
        painelControle.add(campoNumero);
        painelControle.add(labelModo);
        painelControle.add(comboModo);
        painelControle.add(botaoInserir);
        painelControle.add(botaoLimpar);
        painelControle.add(botaoSalvar);
        painelControle.add(botaoImportar);
        painelControle.add(botaoInfo);
        painelControle.add(botaoInverter);
        painelControle.add(botaoQuestoesAVL);

        add(painelControle, BorderLayout.NORTH);

        botaoInserir.addActionListener(e -> inserirValor());
        botaoLimpar.addActionListener(e -> limparArvore());
        botaoSalvar.addActionListener(e -> salvarArvore());
        botaoImportar.addActionListener(e -> importarArvore());
        botaoInfo.addActionListener(e -> mostrarInfo());
        botaoInverter.addActionListener(e -> inverterArvore());
        botaoQuestoesAVL.addActionListener(e -> mostrarQuestoesAVL());
        campoNumero.addActionListener(e -> inserirValor());
        comboModo.addActionListener(e -> trocarModo());
    }

    private void montarPainelArvore() {
        painelArvore = new PainelArvore(arvore);
        JScrollPane scrollPane = new JScrollPane(painelArvore);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void montarPainelLog() {
        areaLog = new JTextArea(9, 30);
        areaLog.setEditable(false);
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setText("Modo atual: Binaria Normal.");

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createTitledBorder("Historico"));
        add(scroll, BorderLayout.SOUTH);
    }

    private void trocarModo() {
        arvore.setModoAVL("AVL".equals(comboModo.getSelectedItem()));
        arvore.registrarAcao("Modo atual: " + arvore.getModoTexto() + ".");
        atualizarAreaLog();
        atualizarVisualizacao();
    }

    private void atualizarComboModo() {
        if (arvore.isModoAVL()) {
            comboModo.setSelectedItem("AVL");
        } else {
            comboModo.setSelectedItem("Binaria Normal");
        }
    }

    private void inserirValor() {
        try {
            String texto = campoNumero.getText().trim();
            if (texto.isEmpty()) {
                return;
            }

            int numero = Integer.parseInt(texto);
            if (numero == -1) {
                JOptionPane.showMessageDialog(this, "O valor -1 nao e permitido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ResultadoInsercao resultado = arvore.inserir(numero);
            atualizarAreaLog();

            if (!resultado.inserido) {
                JOptionPane.showMessageDialog(this, resultado.mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            campoNumero.setText("");
            atualizarVisualizacao();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite apenas numeros inteiros.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparArvore() {
        arvore.limpar();
        areaLog.setText("Arvore limpa.\nHistorico limpo.");
        atualizarVisualizacao();
    }

    private void salvarArvore() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Arvore em TXT");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".txt")) {
                    path += ".txt";
                }

                arvore.salvarEmTxt(path);
                arvore.registrarAcao("Arvore salva em TXT.");
                atualizarAreaLog();
                JOptionPane.showMessageDialog(this, "Arvore salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar arquivo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarArvore() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importar Arvore de TXT");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                arvore.importarDeTxt(path);
                atualizarComboModo();
                atualizarAreaLog();
                atualizarVisualizacao();
                JOptionPane.showMessageDialog(this, "Arvore importada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao importar arquivo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void inverterArvore() {
        if (arvore.getRaiz() == null) {
            JOptionPane.showMessageDialog(this, "Arvore vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (arvore.isModoAVL()) {
            String mensagem = "A inversao nao esta disponivel no modo AVL, pois quebra a regra da arvore de busca.";
            arvore.registrarAcao(mensagem);
            atualizarAreaLog();
            JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        arvore.inverter();
        arvore.registrarAcao("Arvore invertida.");
        atualizarAreaLog();
        atualizarVisualizacao();
    }

    private void mostrarInfo() {
        if (arvore.getRaiz() == null) {
            JOptionPane.showMessageDialog(this, "Arvore vazia.", "Informacao", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Geral ---\n");
        sb.append("Modo atual: ").append(arvore.getModoTexto()).append("\n");
        sb.append("Tipo da arvore: ").append(arvore.getTipoArvore()).append("\n");
        sb.append("Nos da arvore: ").append(arvore.emOrdem()).append("\n");
        sb.append("Quantidade de nos: ").append(arvore.getQuantidadeNos()).append("\n");
        sb.append("Altura da arvore: ").append(arvore.getAltura()).append("\n");
        sb.append("Nivel da arvore: ").append(arvore.getNivelArvore()).append("\n");
        sb.append("Profundidade da arvore: ").append(arvore.getProfundidadeArvore()).append("\n");
        sb.append("Representacao parenteses: ").append(arvore.getRepresentacaoParenteses()).append("\n");
        sb.append("\n--- Percursos ---\n");
        sb.append("Pre-Ordem: ").append(arvore.preOrdem()).append("\n");
        sb.append("Em-Ordem: ").append(arvore.emOrdem()).append("\n");
        sb.append("Pos-Ordem: ").append(arvore.posOrdem()).append("\n");
        sb.append("\n--- Detalhes por No ---\n");
        sb.append(arvore.getDetalhesNosTexto()).append("\n\n");
        sb.append(arvore.getHistoricoInsercoesTexto()).append("\n\n");
        sb.append(arvore.getHistoricoRotacoesTexto()).append("\n\n");
        sb.append("Log geral:\n").append(arvore.getHistoricoGeralTexto());

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(700, 450));

        JOptionPane.showMessageDialog(this, scroll, "Informacoes da Arvore", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarQuestoesAVL() {
        String texto = "1. O que e uma arvore AVL?\n"
                + "Resposta: E uma arvore binaria de busca que mantem o balanceamento dos nos.\n\n"
                + "2. Como e calculado o fator de balanco?\n"
                + "Resposta: Altura da subarvore esquerda menos altura da subarvore direita.\n\n"
                + "3. Quando uma rotacao e necessaria?\n"
                + "Resposta: Quando o fator de balanco fica menor que -1 ou maior que 1.\n\n"
                + "4. Quais sao os tipos de rotacao?\n"
                + "Resposta: Simples a direita, simples a esquerda, dupla a direita e dupla a esquerda.\n\n"
                + "5. O que significa balanco 0?\n"
                + "Resposta: As duas subarvores tem a mesma altura.";

        JTextArea textArea = new JTextArea(texto);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(520, 300));

        JOptionPane.showMessageDialog(this, scroll, "Questoes AVL", JOptionPane.INFORMATION_MESSAGE);
    }

    private void atualizarAreaLog() {
        areaLog.setText(arvore.getHistoricoGeralTexto());
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void atualizarVisualizacao() {
        painelArvore.revalidate();
        painelArvore.repaint();
    }
}
