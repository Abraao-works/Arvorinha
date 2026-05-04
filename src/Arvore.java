import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Arvore {

    private No raiz;
    private boolean modoAVL;
    private final List<String> historicoInsercoes;
    private final List<String> historicoRotacoes;
    private final List<String> historicoGeral;

    public Arvore() {
        historicoInsercoes = new ArrayList<>();
        historicoRotacoes = new ArrayList<>();
        historicoGeral = new ArrayList<>();
    }

    public ResultadoInsercao inserir(int valor) {
        if (buscar(valor)) {
            String mensagem = "O no " + valor + " ja existe na arvore.";
            historicoInsercoes.add("Tentativa repetida do no " + valor + ".");
            registrarAcao(mensagem);
            return new ResultadoInsercao(false, mensagem);
        }

        historicoInsercoes.add("Inserido o no " + valor + ".");

        if (modoAVL) {
            StringBuilder mensagem = new StringBuilder();
            raiz = inserirAVL(raiz, valor, mensagem);

            if (mensagem.length() == 0) {
                mensagem.append("Valor inserido: ").append(valor).append("\n");
                mensagem.append("A arvore continuou balanceada.\n");
                mensagem.append("Nenhuma rotacao foi necessaria.");
                adicionarLinhasNoLog(mensagem.toString());
            }

            return new ResultadoInsercao(true, mensagem.toString());
        }

        raiz = inserirRec(raiz, valor);

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Valor inserido: ").append(valor).append("\n");
        mensagem.append("Modo atual: Binaria Normal.\n");
        mensagem.append("Nenhum balanceamento foi realizado.");

        adicionarLinhasNoLog(mensagem.toString());
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
            String tipoRotacao = "Rotacao simples para a direita";
            mensagem.append(criarMensagemRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getEsquerda().getValor()));
            registrarRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getEsquerda().getValor());
            return rotacaoDireita(no);
        }

        if (balanco < -1 && valor > no.getDireita().getValor()) {
            String tipoRotacao = "Rotacao simples para a esquerda";
            mensagem.append(criarMensagemRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getDireita().getValor()));
            registrarRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getDireita().getValor());
            return rotacaoEsquerda(no);
        }

        if (balanco > 1 && valor > no.getEsquerda().getValor()) {
            String tipoRotacao = "Rotacao dupla a direita";
            mensagem.append(criarMensagemRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getEsquerda().getValor(), no.getEsquerda().getDireita().getValor()));
            registrarRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getEsquerda().getValor(), no.getEsquerda().getDireita().getValor());
            no.setEsquerda(rotacaoEsquerda(no.getEsquerda()));
            return rotacaoDireita(no);
        }

        if (balanco < -1 && valor < no.getDireita().getValor()) {
            String tipoRotacao = "Rotacao dupla a esquerda";
            mensagem.append(criarMensagemRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getDireita().getValor(), no.getDireita().getEsquerda().getValor()));
            registrarRotacao(valor, no.getValor(), balanco, tipoRotacao,
                    no.getValor(), no.getDireita().getValor(), no.getDireita().getEsquerda().getValor());
            no.setDireita(rotacaoDireita(no.getDireita()));
            return rotacaoEsquerda(no);
        }

        return no;
    }

    private void registrarRotacao(int valorInserido, int noDesbalanceado, int balanco,
                                  String tipoRotacao, int... nosAlterados) {
        historicoRotacoes.add("Insercao do " + valorInserido + ": "
                + tipoRotacao.toLowerCase() + " no no " + noDesbalanceado + ".");

        registrarAcao("Valor inserido: " + valorInserido);
        registrarAcao("No desbalanceado: " + noDesbalanceado);
        registrarAcao("Fator de balanco: " + balanco);
        registrarAcao("Tipo de rotacao: " + tipoRotacao);
        registrarAcao("Nos alterados: " + montarTextoNosAlterados(nosAlterados));
    }

    private String criarMensagemRotacao(int valorInserido, int noDesbalanceado, int balanco,
                                        String tipoRotacao, int... nosAlterados) {
        StringBuilder texto = new StringBuilder();
        texto.append("Valor inserido: ").append(valorInserido).append("\n");
        texto.append("No desbalanceado: ").append(noDesbalanceado).append("\n");
        texto.append("Fator de balanco: ").append(balanco).append("\n");
        texto.append("Tipo de rotacao: ").append(tipoRotacao).append("\n");
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

    private String montarTextoNosAlterados(int... nosAlterados) {
        StringBuilder texto = new StringBuilder();

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
        historicoInsercoes.clear();
        historicoRotacoes.clear();
        historicoGeral.clear();
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

    public int getQuantidadeNos() {
        return contarNos(raiz);
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

    public String getHistoricoInsercoesTexto() {
        return montarTextoHistorico("Historico de insercoes:", historicoInsercoes);
    }

    public String getHistoricoRotacoesTexto() {
        return montarTextoHistorico("Historico de rotacoes:", historicoRotacoes);
    }

    public String getHistoricoGeralTexto() {
        if (historicoGeral.isEmpty()) {
            return "Historico vazio.";
        }

        StringBuilder texto = new StringBuilder();
        for (String item : historicoGeral) {
            texto.append(item).append("\n");
        }
        return texto.toString().trim();
    }

    private String montarTextoHistorico(String titulo, List<String> historico) {
        StringBuilder texto = new StringBuilder();
        texto.append(titulo).append("\n");

        if (historico.isEmpty()) {
            texto.append("Nenhum registro.");
            return texto.toString();
        }

        for (int i = 0; i < historico.size(); i++) {
            texto.append(i + 1).append(") ").append(historico.get(i)).append("\n");
        }

        return texto.toString().trim();
    }

    public String getDetalhesNosTexto() {
        if (raiz == null) {
            return "Nenhum no.";
        }

        StringBuilder texto = new StringBuilder();
        escreverDetalhesNos(raiz, texto);
        return texto.toString().trim();
    }

    private void escreverDetalhesNos(No no, StringBuilder texto) {
        if (no == null) {
            return;
        }

        int valor = no.getValor();
        texto.append("No: ").append(valor)
                .append(" | Altura: ").append(no.getAltura())
                .append(" | Nivel: ").append(getNivel(valor))
                .append(" | Profundidade: ").append(getProfundidadeNo(valor))
                .append(" | Balanco: ").append(calcularBalanceamento(no))
                .append(" | Tipo: ").append(no.getTipo().toLowerCase())
                .append("\n");

        escreverDetalhesNos(no.getEsquerda(), texto);
        escreverDetalhesNos(no.getDireita(), texto);
    }

    public void salvarEmTxt(String caminhoArquivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(caminhoArquivo)) {
            writer.println("Modo: " + (modoAVL ? "AVL" : "BINARIA"));
            writer.println("Representacao Parenteses: " + getRepresentacaoParenteses());
            writer.println("Tipo da Arvore: " + getTipoArvore());
            writer.println("Quantidade de Nos: " + getQuantidadeNos());
            writer.println("Altura da Arvore: " + getAltura());
            writer.println("Nivel da Arvore: " + getNivelArvore());
            writer.println("Profundidade da Arvore: " + getProfundidadeArvore());
            writer.println("Pre-Ordem: " + preOrdem());
            writer.println("Em-Ordem: " + emOrdem());
            writer.println("Pos-Ordem: " + posOrdem());
            writer.println();
            writer.println("--- Historico de Insercoes ---");
            escreverListaHistorico(writer, historicoInsercoes);
            writer.println();
            writer.println("--- Historico de Rotacoes ---");
            escreverListaHistorico(writer, historicoRotacoes);
            writer.println();
            writer.println("--- Log Geral ---");
            escreverLogGeral(writer);
            writer.println();
            writer.println("--- Detalhes dos Nos ---");
            writer.println(getDetalhesNosTexto());
        }
    }

    private void escreverListaHistorico(PrintWriter writer, List<String> lista) {
        if (lista.isEmpty()) {
            writer.println("Nenhum registro.");
            return;
        }

        for (int i = 0; i < lista.size(); i++) {
            writer.println((i + 1) + ") " + lista.get(i));
        }
    }

    private void escreverLogGeral(PrintWriter writer) {
        if (historicoGeral.isEmpty()) {
            writer.println("Historico vazio.");
            return;
        }

        for (String item : historicoGeral) {
            writer.println(item);
        }
    }

    public void importarDeTxt(String caminhoArquivo) throws IOException {
        List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo));
        if (linhas.isEmpty()) {
            throw new IOException("Arquivo vazio.");
        }

        boolean modoLido = false;
        modoAVL = false;

        String representacao = null;
        String secaoAtual = "";
        List<String> insercoesImportadas = new ArrayList<>();
        List<String> rotacoesImportadas = new ArrayList<>();
        List<String> logImportado = new ArrayList<>();

        for (String linhaOriginal : linhas) {
            String linha = linhaOriginal.trim();

            if (linha.isEmpty()) {
                continue;
            }

            if (linha.startsWith("Modo:")) {
                String modo = linha.substring(linha.indexOf(':') + 1).trim();
                modoAVL = modo.equalsIgnoreCase("AVL");
                modoLido = true;
                continue;
            }

            if (linha.startsWith("Representacao Parenteses:")) {
                representacao = linha.substring(linha.indexOf(':') + 1).trim();
                continue;
            }

            if (linha.startsWith("--- ")) {
                secaoAtual = linha;
                continue;
            }

            if ("--- Historico de Insercoes ---".equals(secaoAtual)) {
                adicionarItemImportado(insercoesImportadas, linha);
                continue;
            }

            if ("--- Historico de Rotacoes ---".equals(secaoAtual)) {
                adicionarItemImportado(rotacoesImportadas, linha);
                continue;
            }

            if ("--- Log Geral ---".equals(secaoAtual)) {
                if (!linha.equalsIgnoreCase("Historico vazio.")) {
                    logImportado.add(linha);
                }
                continue;
            }

            if (representacao == null && linha.startsWith("(") && linha.endsWith(")")) {
                representacao = linha;
            }
        }

        if (!modoLido) {
            modoAVL = false;
        }

        if (representacao == null) {
            throw new IOException("Representacao da arvore nao encontrada.");
        }

        raiz = parseRepresentacao(representacao);
        historicoInsercoes.clear();
        historicoRotacoes.clear();
        historicoGeral.clear();

        historicoInsercoes.addAll(insercoesImportadas);
        historicoRotacoes.addAll(rotacoesImportadas);
        if (logImportado.isEmpty()) {
            historicoGeral.addAll(insercoesImportadas);
            historicoGeral.addAll(rotacoesImportadas);
        } else {
            historicoGeral.addAll(logImportado);
        }

        String nomeArquivo = Paths.get(caminhoArquivo).getFileName().toString();
        historicoInsercoes.add("Arvore importada do arquivo " + nomeArquivo + ".");
        registrarAcao("Arvore importada de " + nomeArquivo + ".");
    }

    private void adicionarItemImportado(List<String> lista, String linha) {
        String texto = removerNumeracao(linha);

        if (texto.equalsIgnoreCase("Nenhum registro.") || texto.equalsIgnoreCase("Historico vazio.")) {
            return;
        }

        lista.add(texto);
    }

    private String removerNumeracao(String texto) {
        int posicao = texto.indexOf(')');

        if (posicao > 0) {
            boolean soNumeroAntes = true;

            for (int i = 0; i < posicao; i++) {
                if (!Character.isDigit(texto.charAt(i))) {
                    soNumeroAntes = false;
                    break;
                }
            }

            if (soNumeroAntes) {
                return texto.substring(posicao + 1).trim();
            }
        }

        return texto.trim();
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

    public void registrarAcao(String texto) {
        if (texto == null) {
            return;
        }

        String linha = texto.trim();
        if (!linha.isEmpty()) {
            historicoGeral.add(linha);
        }
    }

    private void adicionarLinhasNoLog(String texto) {
        String[] linhas = texto.split("\n");

        for (String linha : linhas) {
            registrarAcao(linha);
        }
    }
}
