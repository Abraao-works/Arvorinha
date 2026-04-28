import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Arvore {

    private No raiz;
    private boolean modoAVL;

    public ResultadoInsercao inserir(int valor) {
        if (buscar(valor)) {
            return new ResultadoInsercao(false, "Numero " + valor + " ja existe na arvore.");
        }

        StringBuilder mensagem = new StringBuilder();

        if (modoAVL) {
            raiz = inserirAVL(raiz, valor, mensagem);

            if (mensagem.length() == 0) {
                mensagem.append("Valor inserido: ").append(valor).append("\n");
                mensagem.append("A arvore continuou balanceada.\n");
                mensagem.append("Nenhuma rotacao foi necessaria.");
            }
        } else {
            raiz = inserirRec(raiz, valor);
            mensagem.append("Valor inserido: ").append(valor).append("\n");
            mensagem.append("Modo atual: Binaria Normal.\n");
            mensagem.append("Nenhum balanceamento foi realizado.");
        }

        return new ResultadoInsercao(true, mensagem.toString());
    }

    private No inserirRec(No no, int valor) {
        if (no == null) {
            return new No(valor);
        }

        if (valor < no.getValor()) {
            no.setEsquerda(inserirRec(no.getEsquerda(), valor));
        } else if (valor > no.getValor()) {
            no.setDireita(inserirRec(no.getDireita(), valor));
        }

        return no;
    }

    private No inserirAVL(No no, int valor, StringBuilder mensagem) {
        if (no == null) {
            return new No(valor);
        }

        if (valor < no.getValor()) {
            no.setEsquerda(inserirAVL(no.getEsquerda(), valor, mensagem));
        } else if (valor > no.getValor()) {
            no.setDireita(inserirAVL(no.getDireita(), valor, mensagem));
        } else {
            return no;
        }

        int balanco = calcularBalanceamento(no);

        if (balanco > 1 && valor < no.getEsquerda().getValor()) {
            mensagem.append(criarMensagemRotacao(
                    valor,
                    no.getValor(),
                    balanco,
                    "Rotacao simples para a direita",
                    no.getValor(),
                    no.getEsquerda().getValor()
            ));
            return rotacaoDireita(no);
        }

        if (balanco < -1 && valor > no.getDireita().getValor()) {
            mensagem.append(criarMensagemRotacao(
                    valor,
                    no.getValor(),
                    balanco,
                    "Rotacao simples para a esquerda",
                    no.getValor(),
                    no.getDireita().getValor()
            ));
            return rotacaoEsquerda(no);
        }

        if (balanco > 1 && valor > no.getEsquerda().getValor()) {
            mensagem.append(criarMensagemRotacao(
                    valor,
                    no.getValor(),
                    balanco,
                    "Rotacao dupla a direita",
                    no.getValor(),
                    no.getEsquerda().getValor(),
                    no.getEsquerda().getDireita().getValor()
            ));
            no.setEsquerda(rotacaoEsquerda(no.getEsquerda()));
            return rotacaoDireita(no);
        }

        if (balanco < -1 && valor < no.getDireita().getValor()) {
            mensagem.append(criarMensagemRotacao(
                    valor,
                    no.getValor(),
                    balanco,
                    "Rotacao dupla a esquerda",
                    no.getValor(),
                    no.getDireita().getValor(),
                    no.getDireita().getEsquerda().getValor()
            ));
            no.setDireita(rotacaoDireita(no.getDireita()));
            return rotacaoEsquerda(no);
        }

        return no;
    }

    private String criarMensagemRotacao(int valorInserido, int noDesbalanceado, int balanco,
                                        String metodo, int... nosAlterados) {
        StringBuilder texto = new StringBuilder();
        texto.append("Valor inserido: ").append(valorInserido).append("\n");
        texto.append("No desbalanceado: ").append(noDesbalanceado).append("\n");
        texto.append("Fator de balanco: ").append(balanco).append("\n");
        texto.append("Metodo usado: ").append(metodo).append("\n");
        texto.append("Nos alterados: ");

        for (int i = 0; i < nosAlterados.length; i++) {
            if (i > 0) {
                if (i == nosAlterados.length - 1) {
                    texto.append(" e ");
                } else {
                    texto.append(", ");
                }
            }
            texto.append(nosAlterados[i]);
        }

        return texto.toString();
    }

    private No rotacaoDireita(No no) {
        No novaRaiz = no.getEsquerda();
        No temp = novaRaiz.getDireita();

        novaRaiz.setDireita(no);
        no.setEsquerda(temp);

        return novaRaiz;
    }

    private No rotacaoEsquerda(No no) {
        No novaRaiz = no.getDireita();
        No temp = novaRaiz.getEsquerda();

        novaRaiz.setEsquerda(no);
        no.setDireita(temp);

        return novaRaiz;
    }

    public boolean buscar(int valor) {
        return buscarRec(raiz, valor);
    }

    private boolean buscarRec(No no, int valor) {
        if (no == null) {
            return false;
        }

        if (valor == no.getValor()) {
            return true;
        }

        if (valor < no.getValor()) {
            return buscarRec(no.getEsquerda(), valor);
        }

        return buscarRec(no.getDireita(), valor);
    }

    public No getRaiz() {
        return raiz;
    }

    public void limpar() {
        raiz = null;
    }

    public boolean isModoAVL() {
        return modoAVL;
    }

    public void setModoAVL(boolean modoAVL) {
        this.modoAVL = modoAVL;
    }

    public String getModoTexto() {
        if (modoAVL) {
            return "AVL";
        }
        return "Binaria Normal";
    }

    public int getAltura() {
        return calcularAltura(raiz);
    }

    private int calcularAltura(No no) {
        if (no == null) {
            return -1;
        }

        return 1 + Math.max(calcularAltura(no.getEsquerda()), calcularAltura(no.getDireita()));
    }

    public int calcularBalanceamento(No no) {
        if (no == null) {
            return 0;
        }

        return calcularAltura(no.getEsquerda()) - calcularAltura(no.getDireita());
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
        List<Integer> lista = new ArrayList<>();
        preOrdemRec(raiz, lista);
        return lista;
    }

    private void preOrdemRec(No no, List<Integer> lista) {
        if (no == null) {
            return;
        }

        lista.add(no.getValor());
        preOrdemRec(no.getEsquerda(), lista);
        preOrdemRec(no.getDireita(), lista);
    }

    public List<Integer> emOrdem() {
        List<Integer> lista = new ArrayList<>();
        emOrdemRec(raiz, lista);
        return lista;
    }

    private void emOrdemRec(No no, List<Integer> lista) {
        if (no == null) {
            return;
        }

        emOrdemRec(no.getEsquerda(), lista);
        lista.add(no.getValor());
        emOrdemRec(no.getDireita(), lista);
    }

    public List<Integer> posOrdem() {
        List<Integer> lista = new ArrayList<>();
        posOrdemRec(raiz, lista);
        return lista;
    }

    private void posOrdemRec(No no, List<Integer> lista) {
        if (no == null) {
            return;
        }

        posOrdemRec(no.getEsquerda(), lista);
        posOrdemRec(no.getDireita(), lista);
        lista.add(no.getValor());
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

    private boolean isCompleta(No no, int indice, int totalNos) {
        if (no == null) {
            return true;
        }

        if (indice >= totalNos) {
            return false;
        }

        return isCompleta(no.getEsquerda(), 2 * indice + 1, totalNos)
                && isCompleta(no.getDireita(), 2 * indice + 2, totalNos);
    }

    private int contarNos(No no) {
        if (no == null) {
            return 0;
        }

        return 1 + contarNos(no.getEsquerda()) + contarNos(no.getDireita());
    }

    public String getRepresentacaoParenteses() {
        StringBuilder texto = new StringBuilder();
        gerarRepresentacaoParenteses(raiz, texto);
        return texto.toString();
    }

    private void gerarRepresentacaoParenteses(No no, StringBuilder texto) {
        if (no == null) {
            texto.append("()");
            return;
        }

        texto.append("(").append(no.getValor());
        if (no.getEsquerda() != null || no.getDireita() != null) {
            texto.append(" ");
            gerarRepresentacaoParenteses(no.getEsquerda(), texto);
            texto.append(" ");
            gerarRepresentacaoParenteses(no.getDireita(), texto);
        }
        texto.append(")");
    }

    public void salvarEmTxt(String caminhoArquivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(caminhoArquivo)) {
            writer.println("Modo: " + (modoAVL ? "AVL" : "BINARIA"));
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
                "No: %d | Altura: %d | Nivel: %d | Profundidade: %d | Balanco: %d | Tipo: %s%n",
                valor,
                no.getAltura(),
                getNivel(valor),
                getProfundidadeNo(valor),
                calcularBalanceamento(no),
                no.getTipo().toLowerCase()
        );

        escreverDetalhesNos(no.getEsquerda(), writer);
        escreverDetalhesNos(no.getDireita(), writer);
    }

    public void importarDeTxt(String caminhoArquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha = reader.readLine();
            if (linha == null) {
                throw new IOException("Arquivo vazio.");
            }

            if (linha.startsWith("Modo:")) {
                String modo = linha.substring(linha.indexOf(':') + 1).trim();
                modoAVL = modo.equalsIgnoreCase("AVL");
                linha = reader.readLine();
            } else {
                modoAVL = false;
            }

            if (linha == null) {
                throw new IOException("Representacao da arvore nao encontrada.");
            }

            String representacao = linha.substring(linha.indexOf(':') + 1).trim();
            raiz = parseRepresentacao(representacao);
        }
    }

    private No parseRepresentacao(String texto) {
        texto = texto.trim();
        if (texto.equals("()")) {
            return null;
        }

        if (texto.startsWith("(") && texto.endsWith(")")) {
            texto = texto.substring(1, texto.length() - 1).trim();
        }

        if (texto.isEmpty()) {
            return null;
        }

        int primeiroEspaco = texto.indexOf(' ');
        if (primeiroEspaco == -1) {
            return new No(Integer.parseInt(texto));
        }

        int valor = Integer.parseInt(texto.substring(0, primeiroEspaco));
        String resto = texto.substring(primeiroEspaco).trim();

        No no = new No(valor);
        List<String> subgrupos = extrairSubgrupos(resto);

        if (subgrupos.size() >= 1) {
            no.setEsquerda(parseRepresentacao(subgrupos.get(0)));
        }
        if (subgrupos.size() >= 2) {
            no.setDireita(parseRepresentacao(subgrupos.get(1)));
        }

        return no;
    }

    private List<String> extrairSubgrupos(String texto) {
        List<String> subgrupos = new ArrayList<>();
        int balanco = 0;
        int inicio = -1;

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            if (c == '(') {
                if (balanco == 0) {
                    inicio = i;
                }
                balanco++;
            } else if (c == ')') {
                balanco--;
                if (balanco == 0 && inicio != -1) {
                    subgrupos.add(texto.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }

        return subgrupos;
    }

    public void inverter() {
        raiz = inverterRec(raiz);
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
}
