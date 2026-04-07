import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Arvore {

    private No raiz;

    public boolean inserir(int valor) {
        if (buscar(valor)) {
            return false;
        }

        raiz = inserirRec(raiz, valor);
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

    public boolean buscar(int valor) {
        return buscarRec(raiz, valor);
    }

    private boolean buscarRec(No nodo, int valor) {
        if (nodo == null) return false;

        if (valor == nodo.getValor()) return true;

        if (valor < nodo.getValor())
            return buscarRec(nodo.getEsquerda(), valor);
        else
            return buscarRec(nodo.getDireita(), valor);
    }

    public No getRaiz() {
        return raiz;
    }

    public void limpar() {
        raiz = null;
    }

    public int getAltura() {
        return calcularAltura(raiz);
    }

    private int calcularAltura(No no) {
        if (no == null) return -1;
        return 1 + Math.max(calcularAltura(no.getEsquerda()), calcularAltura(no.getDireita()));
    }

    // Profundidade da Árvore (mesmo que altura máxima)
    public int getProfundidadeArvore() {
        return getAltura();
    }

    // Nível do Nó
    public int getNivel(int valor) {
        return buscarNivel(raiz, valor, 0);
    }

    private int buscarNivel(No no, int valor, int nivel) {
        if (no == null) return -1;
        if (no.getValor() == valor) return nivel;

        int nivelEsquerda = buscarNivel(no.getEsquerda(), valor, nivel + 1);
        if (nivelEsquerda != -1) return nivelEsquerda;

        return buscarNivel(no.getDireita(), valor, nivel + 1);
    }

    // Profundidade do Nó (distância da raiz ao nó)
    public int getProfundidadeNo(int valor) {
        return getNivel(valor);
    }

    // Percursos
    public List<Integer> preOrdem() {
        List<Integer> resultado = new ArrayList<>();
        preOrdemRec(raiz, resultado);
        return resultado;
    }

    private void preOrdemRec(No no, List<Integer> res) {
        if (no == null) return;
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
        if (no == null) return;
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
        if (no == null) return;
        posOrdemRec(no.getEsquerda(), res);
        posOrdemRec(no.getDireita(), res);
        res.add(no.getValor());
    }

    // Caminho até um nó
    public List<Integer> getCaminho(int valor) {
        List<Integer> caminho = new ArrayList<>();
        buscarCaminho(raiz, valor, caminho);
        return caminho;
    }

    private boolean buscarCaminho(No no, int valor, List<Integer> caminho) {
        if (no == null) return false;
        caminho.add(no.getValor());
        if (no.getValor() == valor) return true;

        if (buscarCaminho(no.getEsquerda(), valor, caminho) || buscarCaminho(no.getDireita(), valor, caminho)) {
            return true;
        }

        caminho.remove(caminho.size() - 1);
        return false;
    }

    // Identificação do Tipo da Árvore
    public String getTipoArvore() {
        if (raiz == null) return "Vazia";

        if (isCheia(raiz)) {
            return "Cheia";
        }

        if (isCompleta(raiz, 0, contarNos(raiz))) {
            return "Completa";
        }

        return "Binária Simples";
    }

    private boolean isCheia(No no) {
        int d = profundidadeEsquerda(no);
        return verificarFolhasMesmoNivel(no, d, 0);
    }

    private int profundidadeEsquerda(No no) {
        int d = 0;
        while (no != null) {
            d++;
            no = no.getEsquerda();
        }
        return d;
    }

    private boolean verificarFolhasMesmoNivel(No no, int d, int nivel) {
        if (no == null) return true;

        if (no.getEsquerda() == null && no.getDireita() == null) {
            return (d == nivel + 1);
        }

        if (no.getEsquerda() == null || no.getDireita() == null) {
            return false;
        }

        return verificarFolhasMesmoNivel(no.getEsquerda(), d, nivel + 1) &&
                verificarFolhasMesmoNivel(no.getDireita(), d, nivel + 1);
    }

    private boolean isCompleta(No no, int index, int totalNos) {
        if (no == null) return true;

        // Se o índice atribuído ao nó for maior ou igual ao total de nós,
        // significa que há uma lacuna na numeração (não está o mais à esquerda possível).
        if (index >= totalNos) return false;

        return isCompleta(no.getEsquerda(), 2 * index + 1, totalNos) &&
                isCompleta(no.getDireita(), 2 * index + 2, totalNos);
    }

    private int contarNos(No no) {
        if (no == null) return 0;
        return 1 + contarNos(no.getEsquerda()) + contarNos(no.getDireita());
    }

    // Representação por Parênteses Aninhados
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

    // Salvar em TXT
    public void salvarEmTxt(String caminhoArquivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(caminhoArquivo)) {
            writer.println("Representação Parênteses: " + getRepresentacaoParenteses());
            writer.println("Tipo da Árvore: " + getTipoArvore());
            writer.println("Altura da Árvore: " + getAltura());
            writer.println("Profundidade da Árvore: " + getProfundidadeArvore());
            writer.println("Pré-Ordem: " + preOrdem());
            writer.println("Em-Ordem: " + emOrdem());
            writer.println("Pós-Ordem: " + posOrdem());
            writer.println("\n--- Detalhes dos Nós ---");
            escreverDetalhesNos(raiz, writer);
        }
    }

    private void escreverDetalhesNos(No no, PrintWriter writer) {
        if (no == null) return;
        int v = no.getValor();
        writer.printf("Valor: %d | Tipo: %s | Nível: %d | Altura: %d | Profundidade: %d | Caminho: %s%n",
                v, no.getTipo(), getNivel(v), no.getAltura(), getProfundidadeNo(v), getCaminho(v));
        escreverDetalhesNos(no.getEsquerda(), writer);
        escreverDetalhesNos(no.getDireita(), writer);
    }

    // Importar de TXT
    public void importarDeTxt(String caminhoArquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha = reader.readLine();
            if (linha != null && linha.startsWith("Representação Parênteses: ")) {
                String representacao = linha.substring("Representação Parênteses: ".length()).trim();
                this.raiz = parseRepresentacao(representacao);
            } else {
                throw new IOException("Formato de arquivo inválido: Representação Parênteses não encontrada na primeira linha.");
            }
        }
    }

    private No parseRepresentacao(String s) {
        s = s.trim();
        if (s.equals("()")) return null;

        // Remove parênteses externos
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1).trim();
        }

        if (s.isEmpty()) return null;

        // O primeiro elemento é o valor do nó
        int primeiroEspaco = s.indexOf(' ');
        if (primeiroEspaco == -1) {
            // Nó folha sem filhos representados explicitamente como () ()
            return new No(Integer.parseInt(s));
        }

        int valor = Integer.parseInt(s.substring(0, primeiroEspaco));
        String resto = s.substring(primeiroEspaco).trim();

        No no = new No(valor);

        // Encontrar os dois subgrupos de parênteses
        List<String> subgrupos = extrairSubgrupos(resto);
        if (subgrupos.size() >= 1) {
            no.setEsquerda(parseRepresentacao(subgrupos.get(0)));
        }
        if (subgrupos.size() >= 2) {
            no.setDireita(parseRepresentacao(subgrupos.get(1)));
        }

        return no;
    }

    private List<String> extrairSubgrupos(String s) {
        List<String> subgrupos = new ArrayList<>();
        int balance = 0;
        int inicio = -1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                if (balance == 0) inicio = i;
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance == 0 && inicio != -1) {
                    subgrupos.add(s.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return subgrupos;
    }public void inverter() {
    raiz = inverterRec(raiz);
}

    private No inverterRec(No no) {
        if (no == null) return null;

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
        if (no == null) return nivel - 1;

        int esq = calcularNivelMaximo(no.getEsquerda(), nivel + 1);
        int dir = calcularNivelMaximo(no.getDireita(), nivel + 1);

        return Math.max(esq, dir);
    }
    
    
}