import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Arvore {

    public static final String MODO_BINARIA = "BINARIA";
    public static final String MODO_AVL = "AVL";

    private No raiz;
    private String modo;
    private String ultimoRelatorioInsercao;

    public Arvore() {
        modo = MODO_BINARIA;
        ultimoRelatorioInsercao = "Modo atual: Binaria Normal.";
    }

    public boolean inserir(int valor) {
        if (buscar(valor)) {
            ultimoRelatorioInsercao = "Valor " + valor + " ja existe na arvore.";
            return false;
        }

        if (isModoAVL()) {
            ultimoRelatorioInsercao = "";
            raiz = inserirAVL(raiz, valor);

            if (ultimoRelatorioInsercao.isEmpty()) {
                ultimoRelatorioInsercao = montarRelatorioSemRotacao(valor);
            }
        } else {
            raiz = inserirRec(raiz, valor);
            ultimoRelatorioInsercao = "Valor inserido: " + valor
                    + "\nModo atual: Binaria Normal."
                    + "\nNenhum balanceamento foi realizado.";
        }

        return true;
    }

    private No inserirRec(No nodo, int valor) {
        if (nodo == null) {
            return new No(valor);
        }

        if (valor < nodo.getValor()) {
            nodo.setEsquerda(inserirRec(nodo.getEsquerda(), valor));
        } else if (valor > nodo.getValor()) {
            nodo.setDireita(inserirRec(nodo.getDireita(), valor));
        }

        return nodo;
    }

    private No inserirAVL(No no, int valor) {
        if (no == null) {
            return new No(valor);
        }

        if (valor < no.getValor()) {
            no.setEsquerda(inserirAVL(no.getEsquerda(), valor));
        } else if (valor > no.getValor()) {
            no.setDireita(inserirAVL(no.getDireita(), valor));
        } else {
            return no;
        }

        int balanceamento = calcularBalanceamento(no);

        if (balanceamento > 1 && valor < no.getEsquerda().getValor()) {
            int valorFilho = no.getEsquerda().getValor();
            ultimoRelatorioInsercao = montarRelatorioRotacao(
                    valor,
                    no.getValor(),
                    balanceamento,
                    "Rotacao simples para a direita",
                    no.getValor(),
                    valorFilho
            );
            return rotacaoDireita(no);
        }

        if (balanceamento < -1 && valor > no.getDireita().getValor()) {
            int valorFilho = no.getDireita().getValor();
            ultimoRelatorioInsercao = montarRelatorioRotacao(
                    valor,
                    no.getValor(),
                    balanceamento,
                    "Rotacao simples para a esquerda",
                    no.getValor(),
                    valorFilho
            );
            return rotacaoEsquerda(no);
        }

        if (balanceamento > 1 && valor > no.getEsquerda().getValor()) {
            int valorFilho = no.getEsquerda().getValor();
            int valorNeto = no.getEsquerda().getDireita().getValor();
            ultimoRelatorioInsercao = montarRelatorioRotacao(
                    valor,
                    no.getValor(),
                    balanceamento,
                    "Rotacao dupla a direita",
                    no.getValor(),
                    valorFilho,
                    valorNeto
            );
            no.setEsquerda(rotacaoEsquerda(no.getEsquerda()));
            return rotacaoDireita(no);
        }

        if (balanceamento < -1 && valor < no.getDireita().getValor()) {
            int valorFilho = no.getDireita().getValor();
            int valorNeto = no.getDireita().getEsquerda().getValor();
            ultimoRelatorioInsercao = montarRelatorioRotacao(
                    valor,
                    no.getValor(),
                    balanceamento,
                    "Rotacao dupla a esquerda",
                    no.getValor(),
                    valorFilho,
                    valorNeto
            );
            no.setDireita(rotacaoDireita(no.getDireita()));
            return rotacaoEsquerda(no);
        }

        return no;
    }

    private No rotacaoDireita(No no) {
        No novaRaiz = no.getEsquerda();
        No subArvore = novaRaiz.getDireita();

        novaRaiz.setDireita(no);
        no.setEsquerda(subArvore);

        return novaRaiz;
    }

    private No rotacaoEsquerda(No no) {
        No novaRaiz = no.getDireita();
        No subArvore = novaRaiz.getEsquerda();

        novaRaiz.setEsquerda(no);
        no.setDireita(subArvore);

        return novaRaiz;
    }

    private String montarRelatorioSemRotacao(int valor) {
        return "Valor inserido: " + valor
                + "\nA arvore continuou balanceada."
                + "\nNenhuma rotacao foi necessaria.";
    }

    private String montarRelatorioRotacao(int valorInserido, int noDesbalanceado, int balanceamento,
                                          String metodo, int... nosAlterados) {
        StringBuilder sb = new StringBuilder();
        sb.append("Valor inserido: ").append(valorInserido).append("\n");
        sb.append("No desbalanceado: ").append(noDesbalanceado).append("\n");
        sb.append("Fator de balanco: ").append(balanceamento).append("\n");
        sb.append("Metodo usado: ").append(metodo).append("\n");
        sb.append("Nos alterados: ");

        for (int i = 0; i < nosAlterados.length; i++) {
            if (i > 0) {
                if (i == nosAlterados.length - 1) {
                    sb.append(" e ");
                } else {
                    sb.append(", ");
                }
            }
            sb.append(nosAlterados[i]);
        }

        return sb.toString();
    }

    public boolean buscar(int valor) {
        return buscarRec(raiz, valor);
    }

    private boolean buscarRec(No nodo, int valor) {
        if (nodo == null) {
            return false;
        }

        if (valor == nodo.getValor()) {
            return true;
        }

        if (valor < nodo.getValor()) {
            return buscarRec(nodo.getEsquerda(), valor);
        }

        return buscarRec(nodo.getDireita(), valor);
    }

    public No getRaiz() {
        return raiz;
    }

    public void limpar() {
        raiz = null;
        ultimoRelatorioInsercao = "Arvore limpa.";
    }

    public int getAltura() {
        return altura(raiz);
    }

    private int altura(No no) {
        if (no == null) {
            return -1;
        }

        return 1 + Math.max(altura(no.getEsquerda()), altura(no.getDireita()));
    }

    public int calcularBalanceamento(No no) {
        if (no == null) {
            return 0;
        }

        return altura(no.getEsquerda()) - altura(no.getDireita());
    }

    public int getProfundidadeArvore() {
        return getAltura();
    }

    public int getNivel(int valor) {
        return buscarNivel(raiz, valor, 0);
    }

    private int buscarNivel(No no, int valor, int nivel) {
        if (no == null) {
            return -1;
        }
        if (no.getValor() == valor) {
            return nivel;
        }

        int nivelEsquerda = buscarNivel(no.getEsquerda(), valor, nivel + 1);
        if (nivelEsquerda != -1) {
            return nivelEsquerda;
        }

        return buscarNivel(no.getDireita(), valor, nivel + 1);
    }

    public int getProfundidadeNo(int valor) {
        return getNivel(valor);
    }

    public List<Integer> preOrdem() {
        List<Integer> resultado = new ArrayList<>();
        preOrdemRec(raiz, resultado);
        return resultado;
    }

    private void preOrdemRec(No no, List<Integer> res) {
        if (no == null) {
            return;
        }
        res.add(no.getValor());
        preOrdemRec(no.getEsquerda(), res);
        preOrdemRec(no.getDireita(), res);
    }

    public List<Integer> emOrdem() {
        List<Integer> resultado = new ArrayList<>();
        emOrdemRec(raiz, resultado);
        return resultado;
    }

    private void emOrdemRec(No no, List<Integer> res) {
        if (no == null) {
            return;
        }
        emOrdemRec(no.getEsquerda(), res);
        res.add(no.getValor());
        emOrdemRec(no.getDireita(), res);
    }

    public List<Integer> posOrdem() {
        List<Integer> resultado = new ArrayList<>();
        posOrdemRec(raiz, resultado);
        return resultado;
    }

    private void posOrdemRec(No no, List<Integer> res) {
        if (no == null) {
            return;
        }
        posOrdemRec(no.getEsquerda(), res);
        posOrdemRec(no.getDireita(), res);
        res.add(no.getValor());
    }

    public List<Integer> getCaminho(int valor) {
        List<Integer> caminho = new ArrayList<>();
        buscarCaminho(raiz, valor, caminho);
        return caminho;
    }

    private boolean buscarCaminho(No no, int valor, List<Integer> caminho) {
        if (no == null) {
            return false;
        }

        caminho.add(no.getValor());
        if (no.getValor() == valor) {
            return true;
        }

        if (buscarCaminho(no.getEsquerda(), valor, caminho) || buscarCaminho(no.getDireita(), valor, caminho)) {
            return true;
        }

        caminho.remove(caminho.size() - 1);
        return false;
    }

    public String getTipoArvore() {
        if (raiz == null) {
            return "Vazia";
        }

        if (isCheia(raiz)) {
            return "Cheia";
        }

        if (isCompleta(raiz, 0, contarNos(raiz))) {
            return "Completa";
        }

        return "Binaria Simples";
    }

    private boolean isCheia(No no) {
        int profundidade = profundidadeEsquerda(no);
        return verificarFolhasMesmoNivel(no, profundidade, 0);
    }

    private int profundidadeEsquerda(No no) {
        int profundidade = 0;
        while (no != null) {
            profundidade++;
            no = no.getEsquerda();
        }
        return profundidade;
    }

    private boolean verificarFolhasMesmoNivel(No no, int profundidade, int nivel) {
        if (no == null) {
            return true;
        }

        if (no.getEsquerda() == null && no.getDireita() == null) {
            return profundidade == nivel + 1;
        }

        if (no.getEsquerda() == null || no.getDireita() == null) {
            return false;
        }

        return verificarFolhasMesmoNivel(no.getEsquerda(), profundidade, nivel + 1)
                && verificarFolhasMesmoNivel(no.getDireita(), profundidade, nivel + 1);
    }

    private boolean isCompleta(No no, int index, int totalNos) {
        if (no == null) {
            return true;
        }

        if (index >= totalNos) {
            return false;
        }

        return isCompleta(no.getEsquerda(), 2 * index + 1, totalNos)
                && isCompleta(no.getDireita(), 2 * index + 2, totalNos);
    }

    private int contarNos(No no) {
        if (no == null) {
            return 0;
        }
        return 1 + contarNos(no.getEsquerda()) + contarNos(no.getDireita());
    }

    public String getRepresentacaoParenteses() {
        StringBuilder sb = new StringBuilder();
        gerarRepresentacaoParenteses(raiz, sb);
        return sb.toString();
    }

    private void gerarRepresentacaoParenteses(No no, StringBuilder sb) {
        if (no == null) {
            sb.append("()");
            return;
        }

        sb.append("(").append(no.getValor());
        if (no.getEsquerda() != null || no.getDireita() != null) {
            sb.append(" ");
            gerarRepresentacaoParenteses(no.getEsquerda(), sb);
            sb.append(" ");
            gerarRepresentacaoParenteses(no.getDireita(), sb);
        }
        sb.append(")");
    }

    public void salvarEmTxt(String caminhoArquivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(caminhoArquivo)) {
            writer.println("Modo: " + modo);
            writer.println("Representacao Parenteses: " + getRepresentacaoParenteses());
            writer.println("Tipo da Arvore: " + getTipoArvore());
            writer.println("Altura da Arvore: " + getAltura());
            writer.println("Profundidade da Arvore: " + getProfundidadeArvore());
            writer.println("Pre-Ordem: " + preOrdem());
            writer.println("Em-Ordem: " + emOrdem());
            writer.println("Pos-Ordem: " + posOrdem());
            writer.println();
            writer.println("--- Detalhes dos Nos ---");
            escreverDetalhesNos(raiz, writer);
        }
    }

    private void escreverDetalhesNos(No no, PrintWriter writer) {
        if (no == null) {
            return;
        }

        int valor = no.getValor();
        writer.printf(
                "Valor: %d | Tipo: %s | Nivel: %d | Altura: %d | Profundidade: %d | Balanco: %d | Caminho: %s%n",
                valor,
                no.getTipo(),
                getNivel(valor),
                no.getAltura(),
                getProfundidadeNo(valor),
                calcularBalanceamento(no),
                getCaminho(valor)
        );

        escreverDetalhesNos(no.getEsquerda(), writer);
        escreverDetalhesNos(no.getDireita(), writer);
    }

    public void importarDeTxt(String caminhoArquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String primeiraLinha = reader.readLine();
            if (primeiraLinha == null) {
                throw new IOException("Arquivo vazio.");
            }

            String linhaRepresentacao = primeiraLinha;

            if (primeiraLinha.startsWith("Modo:")) {
                setModo(primeiraLinha.substring(primeiraLinha.indexOf(':') + 1).trim());
                linhaRepresentacao = reader.readLine();
            } else {
                setModo(MODO_BINARIA);
            }

            if (linhaRepresentacao == null) {
                throw new IOException("Representacao da arvore nao encontrada.");
            }

            String representacao = extrairRepresentacao(linhaRepresentacao);
            if (representacao.isEmpty()) {
                throw new IOException("Representacao da arvore invalida.");
            }

            raiz = parseRepresentacao(representacao);
            ultimoRelatorioInsercao = "Arvore importada com modo " + getModoTexto() + ".";
        }
    }

    private String extrairRepresentacao(String linha) {
        int indiceDoisPontos = linha.indexOf(':');
        if (indiceDoisPontos == -1) {
            return linha.trim();
        }
        return linha.substring(indiceDoisPontos + 1).trim();
    }

    private No parseRepresentacao(String s) {
        s = s.trim();
        if (s.equals("()")) {
            return null;
        }

        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1).trim();
        }

        if (s.isEmpty()) {
            return null;
        }

        int primeiroEspaco = s.indexOf(' ');
        if (primeiroEspaco == -1) {
            return new No(Integer.parseInt(s));
        }

        int valor = Integer.parseInt(s.substring(0, primeiroEspaco));
        String resto = s.substring(primeiroEspaco).trim();

        No no = new No(valor);
        List<String> subgrupos = extrairSubgrupos(resto);

        if (!subgrupos.isEmpty()) {
            no.setEsquerda(parseRepresentacao(subgrupos.get(0)));
        }
        if (subgrupos.size() > 1) {
            no.setDireita(parseRepresentacao(subgrupos.get(1)));
        }

        return no;
    }

    private List<String> extrairSubgrupos(String s) {
        List<String> subgrupos = new ArrayList<>();
        int balance = 0;
        int inicio = -1;

        for (int i = 0; i < s.length(); i++) {
            char caractere = s.charAt(i);
            if (caractere == '(') {
                if (balance == 0) {
                    inicio = i;
                }
                balance++;
            } else if (caractere == ')') {
                balance--;
                if (balance == 0 && inicio != -1) {
                    subgrupos.add(s.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }

        return subgrupos;
    }

    public void inverter() {
        raiz = inverterRec(raiz);
        ultimoRelatorioInsercao = "Arvore invertida no modo Binaria Normal.";
    }

    private No inverterRec(No no) {
        if (no == null) {
            return null;
        }

        No esquerda = inverterRec(no.getEsquerda());
        No direita = inverterRec(no.getDireita());

        no.setEsquerda(direita);
        no.setDireita(esquerda);

        return no;
    }

    public int getNivelArvore() {
        return calcularNivelMaximo(raiz, 0);
    }

    private int calcularNivelMaximo(No no, int nivel) {
        if (no == null) {
            return nivel - 1;
        }

        int esquerda = calcularNivelMaximo(no.getEsquerda(), nivel + 1);
        int direita = calcularNivelMaximo(no.getDireita(), nivel + 1);

        return Math.max(esquerda, direita);
    }

    public String getModo() {
        return modo;
    }

    public String getModoTexto() {
        if (isModoAVL()) {
            return "AVL";
        }
        return "Binaria Normal";
    }

    public boolean isModoAVL() {
        return MODO_AVL.equals(modo);
    }

    public void setModo(String novoModo) {
        modo = normalizarModo(novoModo);
    }

    public void alterarModo(String novoModo) {
        String modoNormalizado = normalizarModo(novoModo);

        if (modo.equals(modoNormalizado)) {
            ultimoRelatorioInsercao = "Modo atual: " + getModoTexto() + ".";
            return;
        }

        List<Integer> valores = preOrdem();
        raiz = null;
        modo = modoNormalizado;

        for (int valor : valores) {
            inserir(valor);
        }

        if (valores.isEmpty()) {
            ultimoRelatorioInsercao = "Modo alterado para " + getModoTexto() + ".";
        } else {
            ultimoRelatorioInsercao = "Modo alterado para " + getModoTexto()
                    + ".\nA arvore foi montada de novo com os valores atuais.";
        }
    }

    public String getUltimoRelatorioInsercao() {
        return ultimoRelatorioInsercao;
    }

    private String normalizarModo(String novoModo) {
        if (MODO_AVL.equalsIgnoreCase(novoModo)) {
            return MODO_AVL;
        }
        return MODO_BINARIA;
    }
}
