import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TelaArvore extends JFrame {

    private Arvore arvore;
    private JTextField campoNumero;
    private JComboBox<String> comboModo;
    private JTextArea areaMensagem;
    private PainelArvore painelArvore;

    public TelaArvore() {
        arvore = new Arvore();

        setTitle("Arvore Binaria");
        setSize(1000, 730);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        montarPainelControle();
        montarPainelArvore();
        montarPainelMensagem();
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

        add(painelControle, BorderLayout.NORTH);

        botaoInserir.addActionListener(e -> inserirValor());
        botaoLimpar.addActionListener(e -> limparArvore());
        botaoSalvar.addActionListener(e -> salvarArvore());
        botaoImportar.addActionListener(e -> importarArvore());
        botaoInfo.addActionListener(e -> mostrarInfo());
        botaoInverter.addActionListener(e -> inverterArvore());
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

    private void montarPainelMensagem() {
        areaMensagem = new JTextArea(5, 30);
        areaMensagem.setEditable(false);
        areaMensagem.setLineWrap(true);
        areaMensagem.setWrapStyleWord(true);
        areaMensagem.setText("Modo atual: Binaria Normal.");

        JScrollPane scroll = new JScrollPane(areaMensagem);
        scroll.setBorder(BorderFactory.createTitledBorder("Ultima acao"));
        add(scroll, BorderLayout.SOUTH);
    }

    private void trocarModo() {
        arvore.setModoAVL("AVL".equals(comboModo.getSelectedItem()));
        areaMensagem.setText("Modo atual: " + arvore.getModoTexto() + ".");
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
            areaMensagem.setText(resultado.mensagem);

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
        areaMensagem.setText("Arvore limpa.");
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
                areaMensagem.setText("Arvore salva com sucesso.");
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
                areaMensagem.setText("Arvore importada com modo " + arvore.getModoTexto() + ".");
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
            areaMensagem.setText(mensagem);
            JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        arvore.inverter();
        areaMensagem.setText("Arvore invertida.");
        atualizarVisualizacao();
    }

    private void mostrarInfo() {
        if (arvore.getRaiz() == null) {
            JOptionPane.showMessageDialog(this, "Arvore vazia.", "Informacao", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Geral ---\n");
        sb.append("Modo da Arvore: ").append(arvore.getModoTexto()).append("\n");
        sb.append("Tipo da Arvore: ").append(arvore.getTipoArvore()).append("\n");
        sb.append("Altura da Arvore: ").append(arvore.getAltura()).append("\n");
        sb.append("Nivel da Arvore: ").append(arvore.getNivelArvore()).append("\n");
        sb.append("Profundidade da Arvore: ").append(arvore.getProfundidadeArvore()).append("\n");
        sb.append("\n--- Percursos ---\n");
        sb.append("Pre-Ordem: ").append(arvore.preOrdem()).append("\n");
        sb.append("Em-Ordem: ").append(arvore.emOrdem()).append("\n");
        sb.append("Pos-Ordem: ").append(arvore.posOrdem()).append("\n");
        sb.append("Representacao Parenteses: ").append(arvore.getRepresentacaoParenteses()).append("\n");
        sb.append("\n--- Detalhes por No ---\n");

        adicionarDetalhesNos(arvore.getRaiz(), sb);

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(620, 420));

        JOptionPane.showMessageDialog(this, scroll, "Estatisticas da Arvore", JOptionPane.INFORMATION_MESSAGE);
    }

    private void adicionarDetalhesNos(No no, StringBuilder sb) {
        if (no == null) {
            return;
        }

        int valor = no.getValor();
        sb.append(String.format(
                "No: %-4d | Altura: %d | Nivel: %d | Profundidade: %d | Balanco: %d | Tipo: %s%n",
                valor,
                no.getAltura(),
                arvore.getNivel(valor),
                arvore.getProfundidadeNo(valor),
                arvore.calcularBalanceamento(no),
                no.getTipo().toLowerCase()
        ));

        adicionarDetalhesNos(no.getEsquerda(), sb);
        adicionarDetalhesNos(no.getDireita(), sb);
    }

    private void atualizarVisualizacao() {
        painelArvore.revalidate();
        painelArvore.repaint();
    }
}
